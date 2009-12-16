/*
 * This file is part of ###PROJECT_NAME###
 *
 * Copyright (C) 2009 Fundación para o Fomento da Calidade Industrial e
 *                    Desenvolvemento Tecnolóxico de Galicia
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

package org.navalplanner.business.users.daos;

import org.navalplanner.business.common.daos.IGenericDAO;
import org.navalplanner.business.common.exceptions.InstanceNotFoundException;
import org.navalplanner.business.users.entities.User;

/**
 * DAO interface for the <code>User</code> entity.
 *
 * @author Fernando Bellas Permuy <fbellas@udc.es>
 */
public interface IUserDAO extends IGenericDAO<User, Long>{

    /**
     * NOTE: Login name comparison is case-insensitive.
     */
    public User findByLoginName(String loginName)
        throws InstanceNotFoundException;

    /**
     * NOTE: Login name comparison is case-insensitive, and the method is
     * executed in another transaction.
     */
    public User findByLoginNameAnotherTransaction(String loginName)
        throws InstanceNotFoundException;

    /**
     * NOTE: Login name comparison is case-insensitive.
     */
    public boolean existsByLoginName(String loginName);

    /**
     * NOTE: Login name comparison is case-insensitive, and the method is
     * executed in another transaction.
     */
    public boolean existsByLoginNameAnotherTransaction(String loginName);

}
