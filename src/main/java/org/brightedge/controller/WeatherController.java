package org.brightedge.controller;

import org.brightedge.model.SearchHistory;
import org.brightedge.model.User;
import org.brightedge.model.WeatherResponse;
import org.brightedge.repository.UserRepository;
import org.brightedge.service.SearchHistoryService;
import org.brightedge.service.WeatherService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class WeatherController {

    private final SearchHistoryService historyService;
    private final UserRepository userRepository;
    private final WeatherService weatherService;

    public WeatherController(SearchHistoryService historyService,
                             UserRepository userRepository,
                             WeatherService weatherService) {
        this.historyService = historyService;
        this.userRepository = userRepository;
        this.weatherService = weatherService;
    }

    @GetMapping("/")
    public String index(Model model, Authentication authentication) {
        boolean hasHistory = false;

        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username).orElse(null);
            model.addAttribute("username",username);

            if (user != null) {
                List<SearchHistory> recentSearches = historyService.getRecentSearches(user);
                hasHistory = !recentSearches.isEmpty();
                model.addAttribute("recentCities", recentSearches);
                System.out.println("User: " + username + " | History count: " + recentSearches.size());
            }
        }

        model.addAttribute("hasHistory", hasHistory);
        return "index";
    }
    @GetMapping("/search")
    public String search(@RequestParam String city,
                         Authentication authentication,
                         Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            model.addAttribute("username", username);
            User user = userRepository.findByUsername(username).orElse(null);
            if (user != null) {
                try {
                    // Fetch weather data
                    WeatherResponse weather = weatherService.getWeather(city);
                    model.addAttribute("weather", weather);

                    // Save successful search
                    historyService.addSearch(city, weather.getSys().getCountry(), user);
                } catch (ResponseStatusException ex) {

                    model.addAttribute("error", ex.getReason());
                }
                // Always show recent searches
                List<SearchHistory> recentSearches = historyService.getRecentSearches(user);
                model.addAttribute("recentCities", recentSearches);
            }
        }
        return "index";
    }

    @PostMapping("/clear-history")
    public String clearHistory(Authentication authentication, RedirectAttributes redirectAttributes) {
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username).orElse(null);
            if (user != null) {
                historyService.clearHistory(user);
                redirectAttributes.addFlashAttribute("message", "Search history cleared successfully!");
            }
        }
        // always redirect to root, will recompute hasHistory=false
        return "redirect:/";
    }
}