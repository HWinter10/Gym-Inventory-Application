/*
 * Purpose:
 * - defines system user roles
 * 
 * Function:
 * - identifies access level for each user
 * - supports role based access control decisions
 * 
 * Dependencies:
 * - none
 */

package com.hwinterton.gyminventory.domain;

public enum Role {
    OWNER,
    MANAGER,
    STAFF
}