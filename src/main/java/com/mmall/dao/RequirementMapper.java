package com.mmall.dao;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Requirement;

import java.util.List;

public interface RequirementMapper {
    Requirement getRequirementsDetailsById(Integer requirementsId);

    List<Requirement> findAllRequirements();

    int add_requirements(Requirement requirements);
}
