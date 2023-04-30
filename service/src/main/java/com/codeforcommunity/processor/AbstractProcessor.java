package com.codeforcommunity.processor;

import com.codeforcommunity.enums.PrivilegeLevel;
import com.codeforcommunity.exceptions.AuthException;
import com.codeforcommunity.propertiesLoader.PropertiesLoader;

public abstract class AbstractProcessor {
  /**
   * Throws an exception if the user is not an admin or super admin.
   *
   * @param level the privilege level of the user calling the route
   */
  protected void assertAdminOrSuperAdmin(PrivilegeLevel level) {
    if (!(level.equals(PrivilegeLevel.ADMIN) || level.equals(PrivilegeLevel.SUPER_ADMIN))) {
      throw new AuthException("User does not have the required privilege level.");
    }
  }

  /** Checks and returns whether the website is a Speak for the Trees or Cambridge website. */
  protected boolean isSFTT() {
    return Boolean.parseBoolean(PropertiesLoader.loadProperty("is_sftt"));
  }
}
