package org.gfg.DigitalLibrary.controller;

import jakarta.validation.Valid;
import org.gfg.DigitalLibrary.request.StudentCreationRequest;
import org.gfg.DigitalLibrary.response.Response;
import org.gfg.DigitalLibrary.response.StudentCreationResponse;
import org.gfg.DigitalLibrary.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/student")
public class StudentController {

    @Autowired
    StudentService studentService;

    @PostMapping("/create/student")
    public ResponseEntity<StudentCreationResponse> createStudent(@Valid @RequestBody StudentCreationRequest studentCreationRequest) {
        if (studentCreationRequest == null) {
            StudentCreationResponse response = new StudentCreationResponse();
            response.setStatus("Invalid request");
            response.setMessage("Request is empty / blank");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        StudentCreationResponse studentCreationResponse = studentService.createStudent(studentCreationRequest);

        return new ResponseEntity<>(studentCreationResponse, HttpStatus.OK);
    }
}
