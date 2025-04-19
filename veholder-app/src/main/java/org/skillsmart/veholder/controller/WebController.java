package org.skillsmart.veholder.controller;

import org.skillsmart.veholder.entity.*;
import org.skillsmart.veholder.entity.dto.EnterpriseDto;
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

import java.security.Principal;
import java.util.List;

@Controller
public class WebController {

    @Autowired
    private VehicleService vehicleService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private EnterpriseService enterpriseService;
    @Autowired
    private DriverService driverService;
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
        List<Brand> brands = brandService.getList(sortBrands);
        Sort sortEnterprises = Sort.by("id").ascending();
        List<Enterprise> enterprises = enterpriseService.getEnterprises();
        Sort sortDrivers = Sort.by("id").ascending();
        List<Driver> drivers = driverService.getDrivers(sortDrivers);

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
        List<Brand> brands = brandService.getList(sortBrands);
        model.addAttribute("brands", brands);
        List<Enterprise> enterprises = enterpriseService.getEnterprises();
        model.addAttribute("enterprises", enterprises);
        return "vehicle";
    }

    @RequestMapping("/vehicle/edit")
    public String editVehicle(Model model, @RequestParam Long id) {
        Vehicle vehicle = vehicleService.getVehicleById(id);
        model.addAttribute("vehicle", vehicle);
        Sort sortBrands = Sort.by("name").ascending();
        List<Brand> brands = brandService.getList(sortBrands);
        model.addAttribute("brands", brands);
        List<Enterprise> enterprises = enterpriseService.getEnterprises();
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
        Brand brand = brandService.getBrandById(id);
        model.addAttribute("brand", brand);
        return "brand";
    }

    @RequestMapping("brand/delete")
    public String delBrand(Model model, @RequestParam Long id) {
        brandService.delete(id);
        return "redirect:/info";
    }

    @RequestMapping(value = "brand/save", method = RequestMethod.POST)
    public String saveBrand(@ModelAttribute("brand") Brand brand) {
        brandService.save(brand);
        return "redirect:/info";
    }

    @PostMapping("enterprise/add")
    public String addEnterprise(Model model) {
        Enterprise enterprise = new Enterprise();
        model.addAttribute("enterprise", enterprise);
        return "enterprise";
    }

    @RequestMapping("enterprise/edit")
    public String editEnterprise(Model model, @RequestParam Long id) {
        Enterprise enterprise = enterpriseService.getEnterpriseById(id);
        model.addAttribute("enterprise", enterprise);
        return "enterprise";
    }

    @RequestMapping("enterprise/delete")
    public String delEnterprise(Model model, @RequestParam Long id) {
        enterpriseService.delete(id);
        return "redirect:/info";
    }

    @RequestMapping(value = "enterprise/save", method = RequestMethod.POST)
    public String saveEnterprise(@ModelAttribute("enterprise") Enterprise enterprise) {
        enterpriseService.save(enterprise);
        return "redirect:/info";
    }

    @PostMapping("driver/add")
    public String addDriver(Model model) {
        Driver driver = new Driver();
        List<Enterprise> enterprises = enterpriseService.getEnterprises();
        Sort sortVehicles = Sort.by("id").ascending();
        List<Vehicle> vehicles = vehicleService.getList(sortVehicles);
        model.addAttribute("driver", driver);
        model.addAttribute("enterprises", enterprises);
        model.addAttribute("vehicles", vehicles);
        return "driver";
    }

    @RequestMapping("driver/edit")
    public String editDriver(Model model, @RequestParam Long id) {
        Driver driver = driverService.getDriverById(id);
        List<Enterprise> enterprises = enterpriseService.getEnterprises();
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
        driverService.delete(id);
        return "redirect:/info";
    }

    @RequestMapping(value = "driver/save", method = RequestMethod.POST)
    public String saveDriver(@ModelAttribute("driver") Driver driver) {
        driverService.save(driver);
        return "redirect:/info";
    }

    @GetMapping("/enterprises")
    public String showEnterprises(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        // Имя менеджера (можно получить из БД или JWT)
        //Manager manager = managerRepo.findByUsername(principal.getName()).orElse(new Manager()); // Замените на реальные данные
        //String managerName = manager.getFullName();
        Manager manager = managerRepo.findByUsername("man3").orElse(new Manager()); // Замените на реальные данные
        String managerName = manager.getFullName();
        //String managerName = "тестовый Василий";
        model.addAttribute("managerName", managerName);

        // Список предприятий (пример статических данных)
        List<EnterpriseDto> enterprises = enterpriseService.getEnterprisesByManager(manager.getUsername());
        model.addAttribute("enterprises", enterprises);

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
