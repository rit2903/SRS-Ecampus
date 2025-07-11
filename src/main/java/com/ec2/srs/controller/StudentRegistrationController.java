package com.ec2.srs.controller;

import com.ec2.srs.RegistrationConstants;
import com.ec2.srs.config.RegistrationDeadlineConfig;
import com.ec2.srs.model.*;
import com.ec2.srs.service.*;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
public class StudentRegistrationController {

    @Autowired
    private StudentRegistrationService registrationService;

    @Autowired
    private RegistrationDeadlineConfig deadlineConfig;

    @GetMapping("/srs")
    public String listStudentRegistrations(
            // @RequestParam(name = RegistrationConstants.PARAM_STUDENT_ID, required = true) Long studentId,
            HttpSession session,
            Model model) {

        Long studentId = (Long) session.getAttribute("studentId");

        Students st = registrationService.getStudentById(studentId);

        List<Semesters> smt = registrationService.getSemesterById(st.getStdbchid());

        List<StudentRegistrations> regs = registrationService.getRegistrationsByStudentId(studentId);

        Map<Short, StudentRegistrations> registrationsMap = regs.stream()
                .collect(Collectors.toMap(StudentRegistrations::getSrgstrid, Function.identity()));
        
        boolean isWithinDeadline = deadlineConfig.isWithinDeadline();

        Short maxStrid = registrationService.getMaxSemesterId(st.getStdbchid());

        model.addAttribute("isWithinDeadline", isWithinDeadline);
        model.addAttribute("maxStrid", maxStrid);
        model.addAttribute("studentbean", st);
        model.addAttribute("semestersbean", smt);
        model.addAttribute(RegistrationConstants.ATTRIBUTESTUDENTREGISTRATIONS,registrationsMap);
        return RegistrationConstants.JSPSTUDENTREGISTRATIONLIST;
    }
}