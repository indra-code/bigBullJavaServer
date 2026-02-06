package com.BigBull;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectPackages({
    "com.BigBull.controller",
    "com.BigBull.exception",
    "com.BigBull.service",
        "com.BigBull.repository",
        "com.BigBull.exception",
        "com.BigBull.config"
})
public class BigBullServerApplicationTests {
}
