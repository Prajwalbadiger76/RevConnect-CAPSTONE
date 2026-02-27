package com.example.demo.controller;

import com.example.demo.service.ConnectionService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/connections")
public class ConnectionController {

    private final ConnectionService connectionService;

    public ConnectionController(ConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    // ================= SEND REQUEST =================
    @PostMapping("/send/{username}")
    public String sendRequest(@PathVariable String username,
                              Authentication authentication) {

        connectionService.sendRequest(authentication.getName(), username);
        return "redirect:/profile/" + username;
    }

    // ================= ACCEPT =================
    @PostMapping("/accept/{id}")
    public String acceptRequest(@PathVariable Long id,
                                Authentication authentication) {

        connectionService.acceptRequest(id, authentication.getName());

        // redirect back to pending page (IMPORTANT FIX)
        return "redirect:/connections/pending";
    }

    // ================= REJECT =================
    @PostMapping("/reject/{id}")
    public String rejectRequest(@PathVariable Long id,
                                Authentication authentication) {

        connectionService.rejectRequest(id, authentication.getName());

        // redirect back to pending page (IMPORTANT FIX)
        return "redirect:/connections/pending";
    }

    // ================= REMOVE CONNECTION =================
    @PostMapping("/remove/{username}")
    public String removeConnection(@PathVariable String username,
                                   Authentication authentication) {

        connectionService.removeConnection(authentication.getName(), username);
        return "redirect:/connections/accepted";
    }

    // ================= VIEW PENDING =================
    @GetMapping("/pending")
    public String showPendingRequests(Model model, Authentication auth){

        String username = auth.getName();

        model.addAttribute("requests",
                connectionService.getPendingRequests(username));

        return "connections";   // your pending page
    }

    // ================= VIEW ACCEPTED =================
    @GetMapping("/accepted")
    public String viewAcceptedConnections(Authentication authentication,
                                          Model model) {

        model.addAttribute("connections",
                connectionService.getAcceptedConnections(authentication.getName()));

        return "accepted-connections";
    }

    // ================= CANCEL SENT =================
    @PostMapping("/cancel/{username}")
    public String cancel(@PathVariable String username,
                         Authentication authentication){

        connectionService.cancelRequest(authentication.getName(),username);
        return "redirect:/connections/sent";
    }

    // ================= VIEW SENT =================
    @GetMapping("/sent")
    public String sentRequests(Authentication authentication, Model model){

        model.addAttribute("sent",
                connectionService.getSentRequests(authentication.getName()));

        return "sent-requests";
    }

    // ================= GLOBAL PENDING COUNT (NAVBAR BADGE) =================
    @ModelAttribute("pendingCount")
    public long pendingCount(Authentication auth){
        if(auth == null) return 0;
        return connectionService.getPendingRequestCount(auth.getName());
    }
    @GetMapping("/pending/count")
    @ResponseBody
    public long getPendingCount(Authentication auth){
        if(auth == null) return 0;
        return connectionService.getPendingRequestCount(auth.getName());
    }
}