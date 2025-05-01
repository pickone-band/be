package com.PickOne.domain.recruitments.model;

import com.PickOne.domain.recruitments.model.entity.Recruitment;
import lombok.Getter;
import org.aspectj.apache.bcel.classfile.Module.Open;

@Getter
public enum Status {
    Recruiting, Recruitment_Complete,
    Recruitment_Stopped
}
