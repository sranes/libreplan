/*
 * This file is part of NavalPlan
 *
 * Copyright (C) 2011 ComtecSF, S.L.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.navalplanner.web.users;

import org.navalplanner.business.common.Registry;
import org.navalplanner.business.users.entities.User;
import org.navalplanner.web.users.bootstrap.MandatoryUser;

/**
 * A class which is used to encapsulate some common behaviour of passwords.
 *
 * @author Cristina Alvarino Perez <cristina.alvarino@comtecsf.es>
 * @author Ignacio Diaz Teijido <ignacio.diaz@comtecsf.es>
 */
public class PasswordUtil {

    private String clearNewPassword;

    public void checkIfChangeDefaultPasswd(User user) {
        if (user.getLoginName().equalsIgnoreCase(
                MandatoryUser.ADMIN.getLoginName())) {
            checkIfChangeDefaultPasswd(MandatoryUser.ADMIN);
            return;
        }
        if (user.getLoginName().equalsIgnoreCase(
                MandatoryUser.USER.getLoginName())) {
            checkIfChangeDefaultPasswd(MandatoryUser.USER);
            return;
        }
        if (user.getLoginName().equalsIgnoreCase(
                MandatoryUser.WSREADER.getLoginName())) {
            checkIfChangeDefaultPasswd(MandatoryUser.WSREADER);
            return;
        }
        if (user.getLoginName().equalsIgnoreCase(
                MandatoryUser.WSWRITER.getLoginName())) {
            checkIfChangeDefaultPasswd(MandatoryUser.WSWRITER);
            return;
        }
    }

    private void checkIfChangeDefaultPasswd(MandatoryUser user) {
        boolean changedPasswd = true;
        if (getClearNewPassword().isEmpty()
                || getClearNewPassword().equals(user.getClearPassword())) {
            changedPasswd = false;
        }
        // save the field changedDefaultAdminPassword in configuration.
        Registry.getConfigurationDAO().saveChangedDefaultPassword(
                user.getLoginName(), changedPasswd);
    }

    public void setClearNewPassword(String clearNewPassword) {
        this.clearNewPassword = clearNewPassword;
    }

    public String getClearNewPassword() {
        return clearNewPassword;
    }

}