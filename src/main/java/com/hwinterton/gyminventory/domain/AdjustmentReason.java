/*
 * Purpose:
 * - defines allowed inventory adjustment reason codes
 * 
 * Function:
 * - standardizes adjustment reasons for accountability and reporting
 * 
 * Dependencies:
 * - none
 */

package com.hwinterton.gyminventory.domain;

public enum AdjustmentReason {
    RESTOCK,
    DAMAGE,
    GIVEAWAY,
    COUNT_CORRECTION,
    EXPIRED,
    OTHER
}