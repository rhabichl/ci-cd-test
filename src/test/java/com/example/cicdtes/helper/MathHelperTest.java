package com.example.cicdtes.helper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MathHelperTest {

    @Test
    void add() {
        int a = 1;
        int b = 1;
        Assertions.assertEquals(a+b, MathHelper.add(a,b));
    }
}