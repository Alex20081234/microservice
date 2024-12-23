package com.epam.microservice.service;

import com.epam.microservice.common.EntityNotFoundException;
import com.epam.microservice.domain.TrainerSummary;
import com.epam.microservice.dto.ActionType;
import com.epam.microservice.domain.MonthlyWorkload;
import com.epam.microservice.dto.ResponseSummary;
import com.epam.microservice.domain.YearlyWorkload;
import com.epam.microservice.dto.SubmitWorkloadChangesRequestBody;
import com.epam.microservice.dao.TrainerSummariesDao;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@AllArgsConstructor
public class TrainerSummariesServiceImpl implements TrainerSummariesService {
    private final TrainerSummariesDao dao;

    @Override
    public void submitWorkloadChanges(SubmitWorkloadChangesRequestBody body) {
        if (!dao.exists(body.getTrainerUsername()) && body.getChangeType() == ActionType.DELETE) {
            throw new IllegalArgumentException("Invalid action type for nonexistent trainer");
        }
        dao.updateOrSave(composeSummary(body));
    }

    @Override
    public ResponseSummary getSummary(String username) {
        TrainerSummary summary = dao.getTrainerSummary(username)
                .orElseThrow(() -> new EntityNotFoundException("Trainer summary for username " + username + " was not found"));
        ResponseSummary response = ResponseSummary.builder()
                .username(summary.getUsername())
                .firstName(summary.getFirstName())
                .lastName(summary.getLastName())
                .status(summary.isStatus())
                .build();
        response.setList(summary.getWorkloads());
        return response;
    }

    private TrainerSummary composeSummary(SubmitWorkloadChangesRequestBody body) {
        int hours = body.getTrainingDurationMinutes();
        MonthlyWorkload monthlyWorkload = MonthlyWorkload.builder()
                .month(body.getTrainingDate().getMonth())
                .workingHours(body.getChangeType() == ActionType.ADD ? hours : -hours)
                .build();
        YearlyWorkload yearlyWorkload = YearlyWorkload.builder()
                .year(body.getTrainingDate().getYear())
                .list(List.of(monthlyWorkload))
                .build();
        return TrainerSummary.builder()
                .username(body.getTrainerUsername())
                .firstName(body.getTrainerFirstName())
                .lastName(body.getTrainerLastName())
                .status(body.getTrainerIsActive())
                .workloads(List.of(yearlyWorkload))
                .build();
    }
}
