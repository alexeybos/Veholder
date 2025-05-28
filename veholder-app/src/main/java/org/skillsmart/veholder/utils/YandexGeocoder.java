package org.skillsmart.veholder.utils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONObject;
import org.json.JSONArray;

public class YandexGeocoder {
    public static String getAddressDescByYandex(double lon, double lat) throws Exception {
        /*double lat = 55.751244;
        double lon = 37.618423;*/
        String apiKey = "53095c96-f6eb-4292-9d70-b65a0377f935";

        String url = String.format(
                "https://geocode-maps.yandex.ru/1.x/?format=json&apikey=%s&geocode=%f,%f",
                apiKey, lon, lat  // Яндекс принимает долготу перед широтой!
        );

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JSONObject json = new JSONObject(response.body());
        JSONObject featureMember = json.getJSONObject("response")
                .getJSONObject("GeoObjectCollection")
                .getJSONArray("featureMember")
                .getJSONObject(0);
        JSONObject geoObject = featureMember.getJSONObject("GeoObject");
        //String address =

        //System.out.println("Адрес: " + address);
        //
        //return geoObject.getString("name");
        return geoObject.getJSONObject("metaDataProperty").getJSONObject("GeocoderMetaData").getString("text");
    }
}
