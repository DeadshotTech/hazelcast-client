package com.thatninjaguyspeaks.hazelcast.models;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonV1 {
    int age;
    String name;
}
