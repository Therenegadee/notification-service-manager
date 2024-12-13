package com.github.therenegade.notification.manager.operations.placeholder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class GetPlaceholderInfoRequest {
    private List<Integer> recipientsIds;
}
