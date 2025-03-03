package org.skillsmart.veholder.controller;

import org.skillsmart.veholder.entity.Brand;
import org.skillsmart.veholder.entity.Vehicle;
import org.skillsmart.veholder.service.BrandService;
import org.skillsmart.veholder.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;
    @Autowired
    private BrandService brandService;

    @GetMapping("/info")
    public String getAllInfo(Model model) {
        Sort sortVehicles = Sort.by("id").ascending();
        List<Vehicle> vehicles = vehicleService.getList(sortVehicles);
        Sort sortBrands = Sort.by("id").ascending();
        List<Brand> brands = brandService.getList(sortBrands);
        model.addAttribute("vehicles", vehicles);
        model.addAttribute("brands", brands);
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

    @PostMapping("/vehicle/add")
    public String addVehicle(Model model) {
        Vehicle vehicle = new Vehicle();
        model.addAttribute("vehicle", vehicle);
        Sort sortBrands = Sort.by("name").ascending();
        List<Brand> brands = brandService.getList(sortBrands);
        model.addAttribute("brands", brands);
        return "vehicle";
    }

    @RequestMapping("/vehicle/edit")
    public String editVehicle(Model model, @RequestParam Long id) {
        Vehicle vehicle = vehicleService.getVehicleById(id);
        model.addAttribute("vehicle", vehicle);
        Sort sortBrands = Sort.by("name").ascending();
        List<Brand> brands = brandService.getList(sortBrands);
        model.addAttribute("brands", brands);
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
}
