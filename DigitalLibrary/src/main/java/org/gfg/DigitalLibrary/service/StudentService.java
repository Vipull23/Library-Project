package org.gfg.DigitalLibrary.service;

import org.gfg.DigitalLibrary.model.Address;
import org.gfg.DigitalLibrary.model.Student;
import org.gfg.DigitalLibrary.model.StudentStatus;
import org.gfg.DigitalLibrary.repository.StudentRepository;
import org.gfg.DigitalLibrary.request.StudentCreationRequest;
import org.gfg.DigitalLibrary.response.StudentCreationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class StudentService {

    @Autowired
    StudentRepository studentRepository;


    public StudentCreationResponse createStudent(StudentCreationRequest studentCreationRequest) {
        String name = studentCreationRequest.getName();
        String email = studentCreationRequest.getEmail();
        String mobileNumber = studentCreationRequest.getMobileNumber();
        LocalDate dob = studentCreationRequest.getDob();
//        String dob = studentCreationRequest.getDob();
        Address address = studentCreationRequest.getAddress();

        Student student = Student.builder().name(name).email(email).mobileNumber(mobileNumber).dob(dob).address(address).build();
        student.setStudentStatus(StudentStatus.ACTIVE);

        int rowsUpdated = 0;
        try {
            rowsUpdated = studentRepository.createStudentInDatabase(student);
        } catch (Exception ex) {
            System.out.println("Exception : " + ex);
        }

        StudentCreationResponse studentCreationResponse = new StudentCreationResponse();
        studentCreationResponse.setName(name);
        studentCreationResponse.setEmail(email);

        if (rowsUpdated == 0) {
            studentCreationResponse = new StudentCreationResponse();
            studentCreationResponse.setStatus("Failed");
            studentCreationResponse.setMessage("Data not Inserted");
            return studentCreationResponse;
        }

        studentCreationResponse.setStatus("SUCCESS");
        studentCreationResponse.setMessage("Data Inserted Successfully");
        return studentCreationResponse;
    }
}
