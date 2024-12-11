package com.github.therenegade.notification.manager.operations.placeholder;

import com.github.therenegade.notification.manager.entity.Placeholder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetPlaceholderInfoRequest {
    private List<Integer> recipientsIds;
    private List<Placeholder> placeholders;
}
