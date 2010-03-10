/*
 * This file is part of NavalPlan
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

package org.navalplanner.business.common.entities;

import org.hibernate.validator.AssertTrue;
import org.hibernate.validator.NotEmpty;
import org.hibernate.validator.NotNull;
import org.navalplanner.business.calendars.entities.BaseCalendar;
import org.navalplanner.business.common.BaseEntity;

/**
 * Application configuration variables.
 *
 * @author Manuel Rego Casasnovas <mrego@igalia.com>
 */
public class Configuration extends BaseEntity {

    public static Configuration create() {
        return create(new Configuration());
    }

    private BaseCalendar defaultCalendar;

    private String companyCode;

    private Boolean generateCodeForCriterion;

    private Boolean generateCodeForLabel;

    private Boolean generateCodeForWorkReport;

    private Boolean generateCodeForResources;

    public void setDefaultCalendar(BaseCalendar defaultCalendar) {
        this.defaultCalendar = defaultCalendar;
    }

    @NotNull(message = "default calendar not specified")
    public BaseCalendar getDefaultCalendar() {
        return defaultCalendar;
    }

    public void setCompanyCode(String companyCode) {
        if (companyCode != null) {
            companyCode = companyCode.trim();
        }
        this.companyCode = companyCode;
    }

    @NotEmpty(message = "company code not specified")
    public String getCompanyCode() {
        return companyCode;
    }

    @AssertTrue(message = "company code must not contain white spaces")
    public boolean checkConstraintCompanyCodeWithoutWhiteSpaces() {
        if ((companyCode == null) || (companyCode.isEmpty())) {
            return false;
        }

        return !companyCode.contains(" ");
    }

    public void setGenerateCodeForCriterion(Boolean generateCodeForCriterion) {
        this.generateCodeForCriterion = generateCodeForCriterion;
    }

    public Boolean getGenerateCodeForCriterion() {
        return generateCodeForCriterion;
    }

    public void setGenerateCodeForLabel(Boolean generateCodeForLabel) {
        this.generateCodeForLabel = generateCodeForLabel;
    }

    public Boolean getGenerateCodeForLabel() {
        return generateCodeForLabel;
    }

    public void setGenerateCodeForWorkReport(Boolean generateCodeForWorkReport) {
        this.generateCodeForWorkReport = generateCodeForWorkReport;
    }

    public Boolean getGenerateCodeForWorkReport() {
        return generateCodeForWorkReport;
    }

    public void setGenerateCodeForResources(Boolean generateCodeForResources) {
        this.generateCodeForResources = generateCodeForResources;
    }

    public Boolean getGenerateCodeForResources() {
        return generateCodeForResources;
    }

}