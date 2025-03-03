package org.skillsmart.veholder.controller;

import org.skillsmart.veholder.entity.Brand;
import org.skillsmart.veholder.entity.Vehicle;
import org.skillsmart.veholder.repository.BrandRepository;
import org.skillsmart.veholder.repository.VehicleRepository;
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
    private VehicleRepository vehicleRepository;
    @Autowired
    private BrandRepository brandRepository;

    @GetMapping("/info")
    public String getAllInfo(Model model) {
        Sort sortVehicles = Sort.by("id").ascending();
        List<Vehicle> vehicles = vehicleRepository.findAll(sortVehicles);
        Sort sortBrands = Sort.by("id").ascending();
        List<Brand> brands = brandRepository.findAll(sortBrands);
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
        return "vehicle";
    }

    @PostMapping("/vehicle/edit")
    public String editVehicle(Model model, @RequestParam Long id) {
        Vehicle vehicle = vehicleRepository.getReferenceById(id);
        model.addAttribute("vehicle", vehicle);
        return "vehicle";
    }

    @PostMapping("vehicle/delete")
    public String delVehicle(Model model, @RequestParam Long id) {
        vehicleRepository.deleteById(id);
        return "redirect:/info";
    }

    @PostMapping("brand/add")
    public String addBrand(Model model) {
        Brand brand = new Brand();
        model.addAttribute("brand", brand);
        return "brand";
    }

    @PostMapping("brand/edit")
    public String editBrand(Model model, @RequestParam Long id) {
        Brand brand = brandRepository.getReferenceById(id);
        model.addAttribute("brand", brand);
        return "brand";
    }

    @PostMapping("brand/delete")
    public String delBrand(Model model, @RequestParam Long id) {
        brandRepository.deleteById(id);
        return "redirect:/info";
    }

    @RequestMapping(value = "brand/save", method = RequestMethod.POST)
    public String saveBrand(@ModelAttribute("brand") Brand brand) {
        brandRepository.save(brand);
        return "redirect:/info";
    }

    //@RequestMapping(value = "/save", method = RequestMethod.POST)
    //public String saveCustomer(@ModelAttribute("customer") Customer customer) {
    //    customerService.save(customer);
    //    return "redirect:/";
    //}
}
