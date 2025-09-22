package org.skillsmart.veholder.controller;

import org.skillsmart.veholder.entity.*;
import org.skillsmart.veholder.repository.BrandRepository;
import org.skillsmart.veholder.repository.DriverRepository;
import org.skillsmart.veholder.repository.EnterpriseRepository;
import org.skillsmart.veholder.repository.ManagerRepository;
import org.skillsmart.veholder.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class WebController {

    @Autowired
    private VehicleService vehicleService;
    @Autowired
    private BrandRepository brandRepo;
    @Autowired
    private EnterpriseRepository enterpriseRepo;
    @Autowired
    private DriverRepository driverRepo;
    @Autowired
    private ManagerRepository managerRepo;

    @GetMapping("")
    public String helloPage(Model model) {
        return "hello";
    }

    @GetMapping("/info")
    public String getAllInfo(Model model) {
        Sort sortVehicles = Sort.by("id").ascending();
        List<Vehicle> vehicles = vehicleService.getList(sortVehicles);
        Sort sortBrands = Sort.by("id").ascending();
        List<Brand> brands = brandRepo.findAll(sortBrands);
        Sort sortEnterprises = Sort.by("id").ascending();
        List<Enterprise> enterprises = enterpriseRepo.findAll(sortEnterprises);;
        Sort sortDrivers = Sort.by("id").ascending();
        List<Driver> drivers = driverRepo.findAll(sortDrivers);

        model.addAttribute("vehicles", vehicles);
        model.addAttribute("brands", brands);
        model.addAttribute("enterprises", enterprises);
        model.addAttribute("drivers", drivers);
        return "info";
    }

    @PostMapping("/info/vehicles")
    public String refreshVehicles(Model model) {
        return "redirect:/info";
    }

    @PostMapping("/info/brands")
    public String refreshBrands(Model model) {
        return "redirect:/info";
    }

    @GetMapping("/info/enterprises")
    public String refreshEnterprises(Model model) {
        return "redirect:/info";
    }

    @GetMapping("/info/drivers")
    public String refreshDrivers(Model model) {
        return "redirect:/info";
    }

    @PostMapping("/vehicle/add")
    public String addVehicle(Model model) {
        Vehicle vehicle = new Vehicle();
        model.addAttribute("vehicle", vehicle);
        Sort sortBrands = Sort.by("name").ascending();
        List<Brand> brands = brandRepo.findAll(sortBrands);
        model.addAttribute("brands", brands);
        Sort sortEnterprises = Sort.by("id").ascending();
        List<Enterprise> enterprises = enterpriseRepo.findAll(sortEnterprises);
        model.addAttribute("enterprises", enterprises);
        return "vehicle";
    }

    @RequestMapping("/vehicle/edit")
    public String editVehicle(Model model, @RequestParam Long id) {
        Vehicle vehicle = vehicleService.getVehicleById(id);
        model.addAttribute("vehicle", vehicle);
        Sort sortBrands = Sort.by("name").ascending();
        List<Brand> brands = brandRepo.findAll(sortBrands);
        model.addAttribute("brands", brands);
        List<Enterprise> enterprises = enterpriseRepo.findAll();
        model.addAttribute("enterprises", enterprises);
        return "vehicle";
    }

    @RequestMapping("vehicle/delete")
    public String delVehicle(Model model, @RequestParam Long id) {
        vehicleService.delete(id);
        return "redirect:/info";
    }

    @RequestMapping(value = "vehicle/save", method = RequestMethod.POST)
    public String saveVehicle(@ModelAttribute("vehicle") Vehicle vehicle) {
        vehicleService.save(vehicle);
        return "redirect:/info";
    }

    @PostMapping("brand/add")
    public String addBrand(Model model) {
        Brand brand = new Brand();
        model.addAttribute("brand", brand);
        return "brand";
    }

    @RequestMapping("brand/edit")
    public String editBrand(Model model, @RequestParam Long id) {
        Brand brand = brandRepo.getReferenceById(id);
        model.addAttribute("brand", brand);
        return "brand";
    }

    @RequestMapping("brand/delete")
    public String delBrand(Model model, @RequestParam Long id) {
        brandRepo.deleteById(id);
        return "redirect:/info";
    }

    @RequestMapping(value = "brand/save", method = RequestMethod.POST)
    public String saveBrand(@ModelAttribute("brand") Brand brand) {
        brandRepo.save(brand);
        return "redirect:/info";
    }

    @PostMapping("enterprise/add")
    public String addEnterprise(Model model) {
        Enterprise enterprise = new Enterprise();
        List allTimezones = ZoneId.getAvailableZoneIds().stream()
                .map(ZoneId::of)
                .sorted(Comparator.comparing(ZoneId::getId))
                .toList();
        model.addAttribute("enterprise", enterprise);
        //((ZoneRegion) allTimezones.get(467)).getOffset(0)
        model.addAttribute("allTimezones", ZoneId.getAvailableZoneIds().stream()
                .map(ZoneId::of)
                .sorted(Comparator.comparing(ZoneId::getId))
                .collect(Collectors.toList()));
        return "enterprise";
    }

    @RequestMapping("enterprise/edit")
    public String editEnterprise(Model model, @RequestParam Long id) {
        Enterprise enterprise = enterpriseRepo.getReferenceById(id);
        model.addAttribute("enterprise", enterprise);
        model.addAttribute("allTimezones", ZoneId.getAvailableZoneIds().stream()
                .map(ZoneId::of)
                .sorted(Comparator.comparing(ZoneId::getId))
                .collect(Collectors.toList()));
        return "enterprise";
    }

    @RequestMapping("enterprise/delete")
    public String delEnterprise(Model model, @RequestParam Long id) {
        enterpriseRepo.deleteById(id);
        return "redirect:/info";
    }

    @RequestMapping(value = "enterprise/save", method = RequestMethod.POST)
    public String saveEnterprise(@ModelAttribute("enterprise") Enterprise enterprise) {
        enterpriseRepo.save(enterprise);
        return "redirect:/info";
    }

    @PostMapping("driver/add")
    public String addDriver(Model model) {
        Driver driver = new Driver();
        List<Enterprise> enterprises = enterpriseRepo.findAll();
        Sort sortVehicles = Sort.by("id").ascending();
        List<Vehicle> vehicles = vehicleService.getList(sortVehicles);
        model.addAttribute("driver", driver);
        model.addAttribute("enterprises", enterprises);
        model.addAttribute("vehicles", vehicles);
        return "driver";
    }

    @RequestMapping("driver/edit")
    public String editDriver(Model model, @RequestParam Long id) {
        Driver driver = driverRepo.getReferenceById(id);
        List<Enterprise> enterprises = enterpriseRepo.findAll();
        Sort sortVehicles = Sort.by("id").ascending();
        List<Vehicle> vehicles = vehicleService.getList(sortVehicles);
        vehicles.addFirst(new Vehicle());
        model.addAttribute("driver", driver);
        model.addAttribute("enterprises", enterprises);
        model.addAttribute("vehicles", vehicles);
        return "driver";
    }

    @RequestMapping("driver/delete")
    public String delDriver(Model model, @RequestParam Long id) {
        driverRepo.deleteById(id);
        return "redirect:/info";
    }

    @RequestMapping(value = "driver/save", method = RequestMethod.POST)
    public String saveDriver(@ModelAttribute("driver") Driver driver) {
        driverRepo.save(driver);
        return "redirect:/info";
    }

    @GetMapping("/enterprises")
    public String showEnterprises(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        //Manager manager = managerRepo.findByUsername("man3").orElse(new Manager()); // only for tests
        Manager manager = managerRepo.findByUsername(userDetails.getUsername()).orElse(new Manager());
        String managerName = manager.getFullName();
        model.addAttribute("managerName", managerName);

        // Список предприятий (получение данных из сервиса)
        //List<EnterpriseDto> enterprises = enterpriseService.getEnterprisesByManager(manager.getUsername());
        //model.addAttribute("enterprises", enterprises);

        return "enterprises";
    }

    /*@RequestMapping(value = "vehicle_driver/save", method = RequestMethod.POST)
    public String saveVehicleDriverRelation(@ModelAttribute("vehicle_driver") VehicleDriver relation, Model model) {
        if (relation.getDriver().getEnterprise().getId() != relation.getVehicle().getEnterprise().getId()) {
            model.addAttribute("errorMessage", "Водитель и автомобиль должны принадлежать одному предприятию.");
            return "error";
        }
        try {
            vehicleDriverService.save(relation);
        } catch (DataIntegrityViolationException e) {
            model.addAttribute("errorMessage", "Водитель уже активен на другом автомобиле.");
            return "error";
        }

        return "redirect:/info";
    }*/
}
