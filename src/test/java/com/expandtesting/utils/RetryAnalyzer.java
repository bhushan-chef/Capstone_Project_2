package com.expandtesting.utils;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {

    private int count = 0;
    private static final int MAX_RETRY_COUNT = 2; // It will retry flaky tests up to 2 times

    @Override
    public boolean retry(ITestResult iTestResult) {
        if (!iTestResult.isSuccess()) {
            if (count < MAX_RETRY_COUNT) {
                count++;
                return true;
            }
        }
        return false;
    }
}