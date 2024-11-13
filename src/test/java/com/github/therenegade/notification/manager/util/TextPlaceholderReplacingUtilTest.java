package com.github.therenegade.notification.manager.util;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.swing.*;
import java.util.Map;

@ExtendWith(SpringExtension.class)
public class TextPlaceholderReplacingUtilTest {

    @Test
    void test() {
        // Given
        String receiverNameParam = "receiver_name";
        String receiverNameValue = "Bob";
        String senderNameParam = "sender_name";
        String senderNameValue = "John";
        String someGuyParam = "some_guy";
        String someGuyValue = "Mike";
        String somePlaceParam = "some_place";
        String somePlaceValue = "Donnie's";

        String templatedMessage = String.format("""
                        Hi, ${%s}! I'm ${%s}. Yesterday I saw ${%s} and we went to eat some burgers at
                        ${%s}. It was really awful, so I don't think I'd go there any time. Bye, ${%s}!
                        """,
                receiverNameParam, senderNameParam, someGuyParam, somePlaceParam, receiverNameParam);

        Map<String, Object> params = Map.of(
                receiverNameParam, receiverNameValue,
                senderNameParam, senderNameValue,
                someGuyParam, someGuyValue,
                somePlaceParam, somePlaceValue
        );

        // When
        String result = TextPlaceholderReplacingUtil.replaceAllPlaceholdersInText(templatedMessage, params);

        // Then
        Assertions.assertThat(result)
                .isEqualTo(String.format("""
                        Hi, %s! I'm %s. Yesterday I saw %s and we went to eat some burgers at
                        %s. It was really awful, so I don't think I'd go there any time. Bye, %s!
                        """, receiverNameValue, senderNameValue, someGuyValue, somePlaceValue, receiverNameValue));
    }
}
