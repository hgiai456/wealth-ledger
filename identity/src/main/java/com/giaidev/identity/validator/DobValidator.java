package com.giaidev.identity.validator;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DobValidator implements ConstraintValidator<DobConstraint, LocalDate> {

    private int min;

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) { // Ham khoi tao
        if (Objects.isNull(value)) return true;

        long years = ChronoUnit.YEARS.between(value, LocalDate.now()); // Ham tinh do tuoi

        return years >= min; // Thực hiện kiểm tra độ tuổi đã tính và min đã require trước đó
    }

    @Override
    public void initialize(DobConstraint constraintAnnotation) { // Ham nay xu ly kiem tra xem data co dung khong
        ConstraintValidator.super.initialize(constraintAnnotation);
        min = constraintAnnotation.min();
    }
}