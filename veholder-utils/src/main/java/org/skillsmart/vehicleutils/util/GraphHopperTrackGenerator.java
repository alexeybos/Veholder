package org.skillsmart.vehicleutils.util;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.skillsmart.vehicleutils.util.entity.TrackPoint;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class GraphHopperTrackGenerator {

    private static final String API_KEY = "d577df8a-16d5-41c2-b096-daf659aaf6e8";
    private static final String GH_API_URL = "https://graphhopper.com/api/1/route";

    public static List<TrackPoint> generateTrack(List<String> points, int intervalSeconds) throws IOException {
        // Формируем URL запроса к GraphHopper API
        String url = buildApiUrl(points);

        // Отправляем HTTP-запрос
        String jsonResponse = sendGetRequest(url);

        // Парсим JSON и извлекаем координаты
        List<TrackPoint> track = parseTrackPoints(jsonResponse, intervalSeconds);

        return track;
    }

    private static String buildApiUrl(List<String> points) {
        StringBuilder url = new StringBuilder(GH_API_URL);
        url.append("?key=").append(API_KEY);
        url.append("&vehicle=car");
        url.append("&points_encoded=false"); // Чтобы получить сырые координаты

        for (String point : points) {
            url.append("&point=").append(point);
        }
        System.out.println("[DEBUG] url = " + url.toString());
        return url.toString();
    }

    private static String sendGetRequest(String url) throws IOException {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            CloseableHttpResponse response = client.execute(request);
            return EntityUtils.toString(response.getEntity());
        }
    }

    private static List<TrackPoint> parseTrackPoints(String json, int intervalSeconds) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json);
        JsonNode path = root.path("paths").get(0);
        JsonNode coordinates = path.path("points").path("coordinates");

        List<TrackPoint> track = new ArrayList<>();
        ZonedDateTime startTime = ZonedDateTime.now();

        for (int i = 0; i < coordinates.size(); i++) {
            double lon = coordinates.get(i).get(0).asDouble();
            double lat = coordinates.get(i).get(1).asDouble();
            ZonedDateTime time = startTime.plusSeconds(i * intervalSeconds);

            track.add(new TrackPoint(lat, lon, time));
        }

        return track;
    }

    /*private static void saveToCsv(List<TrackPoint> track, String filename) throws IOException {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("lat,lon,time\n");
            for (TrackPoint point : track) {
                writer.write(String.format("%f,%f,%s\n",
                        point.lat,
                        point.lon,
                        point.time.format(DateTimeFormatter.ISO_DATE_TIME))
                );
            }
        }
    }

    private static void saveToGpx(List<TrackPoint> track, String filename) throws IOException {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.write("<gpx version=\"1.1\">\n");
            writer.write("<trk><name>Test Track</name><trkseg>\n");

            for (TrackPoint point : track) {
                writer.write(String.format(
                        "<trkpt lat=\"%f\" lon=\"%f\"><time>%s</time></trkpt>\n",
                        point.lat,
                        point.lon,
                        point.time.format(DateTimeFormatter.ISO_DATE_TIME))
                );
            }

            writer.write("</trkseg></trk></gpx>");
        }
    }*/
}
