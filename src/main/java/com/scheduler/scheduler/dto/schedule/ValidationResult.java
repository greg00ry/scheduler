package com.scheduler.scheduler.dto.schedule;

import java.util.List;

public record ValidationResult(List<String> violations, List<String> warnings) {
    public boolean hasViolations() { return !violations.isEmpty(); }
    public boolean hasWarnings() {return !warnings.isEmpty(); }
}
