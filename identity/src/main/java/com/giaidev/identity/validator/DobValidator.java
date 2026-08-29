package com.giaidev.identity.validator;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DobValidator implements ConstraintValidator<DobConstraint, LocalDate> {

    private int minimumAge;

    @Override
    public void initialize(DobConstraint annotation) { // Ham nay xu ly kiem tra xem data co dung khong
        this.minimumAge = annotation.min();
    }

    @Override
    public boolean isValid(LocalDate dob, ConstraintValidatorContext context) { // Ham khoi tao
        if (Objects.isNull(dob)) return true;

        LocalDate lastestAllowedDate =  LocalDate.now().minusYears(minimumAge);

        return !dob.isAfter(lastestAllowedDate); // Thực hiện kiểm tra độ tuổi đã tính và min đã require trước đó
    }


}