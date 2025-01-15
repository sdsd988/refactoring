package toby.salon.refactoring.chapter1.dto;

import java.util.ArrayList;
import java.util.List;

public record Invoice(String customer, List<Performance> performances) {
}
