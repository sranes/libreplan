package org.navalplanner.business.test.planner.entities;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.classextension.EasyMock.createNiceMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.junit.Test;
import org.navalplanner.business.calendars.entities.BaseCalendar;
import org.navalplanner.business.calendars.entities.SameWorkHoursEveryDay;
import org.navalplanner.business.planner.entities.GenericDayAssigment;
import org.navalplanner.business.planner.entities.GenericResourceAllocation;
import org.navalplanner.business.planner.entities.ResourcePerDayUnit;
import org.navalplanner.business.planner.entities.Task;
import org.navalplanner.business.resources.entities.Criterion;
import org.navalplanner.business.resources.entities.Resource;
import org.navalplanner.business.resources.entities.Worker;

public class GenericResourceAllocationTest {

    private GenericResourceAllocation genericResourceAllocation;
    private Set<Criterion> criterions;

    private List<Worker> workers;
    private Worker worker1;
    private Worker worker2;
    private Worker worker3;

    private BaseCalendar baseCalendar;
    private Task task;

    private void givenGenericResourceAllocation() {
        task = givenTaskWithCriterions();
        givenGenericResourceAllocationForTask(task);
    }

    private Task givenTaskWithStartAndEnd(Interval interval) {
        Task task = createNiceMock(Task.class);
        setupCriterions(task);

        expect(task.getStartDate()).andReturn(interval.getStart().toDate())
                .anyTimes();
        expect(task.getEndDate()).andReturn(interval.getEnd().toDate())
                .anyTimes();
        expect(task.getCalendar()).andReturn(baseCalendar).anyTimes();
        replay(task);
        return this.task = task;
    }

    private Task givenTaskWithCriterions() {
        Task task = createNiceMock(Task.class);
        setupCriterions(task);
        expect(task.getCalendar()).andReturn(baseCalendar).anyTimes();
        replay(task);
        return this.task = task;
    }

    private void setupCriterions(Task task) {
        expect(task.getCriterions()).andReturn(givenPredefinedCriterions())
                .anyTimes();
    }

    private void givenGenericResourceAllocationForTask(Task task) {
        genericResourceAllocation = GenericResourceAllocation.create(task);
    }

    private Set<Criterion> givenPredefinedCriterions() {
        Set<Criterion> result = new HashSet<Criterion>();
        Criterion criterion1 = createNiceMock(Criterion.class);
        Criterion criterion2 = createNiceMock(Criterion.class);
        replay(criterion1, criterion2);
        result.add(criterion1);
        result.add(criterion2);
        this.criterions = result;
        return result;
    }

    private void givenWorkersWithoutLoadAndWithoutCalendar() {
        worker1 = createNiceMock(Worker.class);
        worker2 = createNiceMock(Worker.class);
        worker3 = createNiceMock(Worker.class);
        setupCalendarIsNull(worker1);
        buildWorkersList();
        replay(worker1, worker2, worker3);
    }

    private void buildWorkersList() {
        workers = new ArrayList<Worker>();
        workers.add(worker1);
        workers.add(worker2);
        workers.add(worker3);
    }

    private Worker createWorkerWithLoad(int hours) {
        Worker result = createNiceMock(Worker.class);
        setupCalendarIsNull(result);
        expect(result.getAssignedHours(isA(LocalDate.class))).andReturn(hours)
                .anyTimes();
        replay(result);
        return result;
    }

    private void givenWorkersWithLoads(int hours1, int hours2, int hours3) {
        worker1 = createWorkerWithLoad(hours1);
        worker2 = createWorkerWithLoad(hours2);
        worker3 = createWorkerWithLoad(hours3);
        buildWorkersList();
    }

    private void setupCalendarIsNull(Resource resource) {
        expect(resource.getCalendar()).andReturn(null).anyTimes();
    }

    private void givenBaseCalendarWithoutExceptions(int hoursPerDay) {
        BaseCalendar baseCalendar = createNiceMock(BaseCalendar.class);
        expect(baseCalendar.getWorkableHours(isA(Date.class))).andReturn(
                hoursPerDay).anyTimes();
        expect(baseCalendar.getWorkableHours(isA(LocalDate.class))).andReturn(
                hoursPerDay).anyTimes();
        replay(baseCalendar);
        this.baseCalendar = baseCalendar;
    }

    @Test
    public void hasTheCriterionsOfTheTask() {
        givenGenericResourceAllocation();
        assertThat(genericResourceAllocation.getCriterions(),
                equalTo(criterions));
    }

    @Test
    public void getOrderedAssignmentsReturnsEmptyListIfNotExistsWorker() {
        givenWorkersWithoutLoadAndWithoutCalendar();
        givenGenericResourceAllocation();
        List<GenericDayAssigment> assigments = genericResourceAllocation
                .getOrderedAssigmentsFor(worker1);
        assertNotNull(assigments);
        assertTrue(assigments.isEmpty());
    }

    @Test
    public void allocatingGeneratesDayAssignmentsForEachDay() {
        final int TASK_DURATION_DAYS = 4;
        givenBaseCalendarWithoutExceptions(8);
        LocalDate start = new LocalDate(2006, 10, 5);
        givenTaskWithStartAndEnd(toInterval(start, Period
                .days(TASK_DURATION_DAYS)));
        givenGenericResourceAllocationForTask(task);
        givenWorkersWithoutLoadAndWithoutCalendar();

        genericResourceAllocation.forResources(Arrays.asList(worker1))
                .allocate(ResourcePerDayUnit.amount(1));

        List<GenericDayAssigment> orderedAssigmentsFor = genericResourceAllocation
                .getOrderedAssigmentsFor(worker1);
        assertThat(orderedAssigmentsFor.size(), equalTo(TASK_DURATION_DAYS));
        for (int i = 0; i < TASK_DURATION_DAYS; i++) {
            assertThat(orderedAssigmentsFor.get(i).getDay(), equalTo(start
                    .plusDays(i)));
        }
    }

    @Test
    public void allocatingSeveralResourcesPerDayHavingJustOneResourceProducesOvertime() {
        LocalDate start = new LocalDate(2006, 10, 5);
        final Integer standardHoursPerDay = SameWorkHoursEveryDay
                .getDefaultWorkingDay().getWorkableHours(start);
        final int TASK_DURATION_DAYS = 4;
        givenBaseCalendarWithoutExceptions(standardHoursPerDay);
        givenTaskWithStartAndEnd(toInterval(start, Period
                .days(TASK_DURATION_DAYS)));
        givenGenericResourceAllocationForTask(task);
        givenWorkersWithoutLoadAndWithoutCalendar();

        genericResourceAllocation.forResources(Arrays.asList(worker1))
                .allocate(ResourcePerDayUnit.amount(2));

        List<GenericDayAssigment> orderedAssigmentsFor = genericResourceAllocation
                .getOrderedAssigmentsFor(worker1);
        assertThat(orderedAssigmentsFor.get(0).getHours(),
                equalTo(standardHoursPerDay * 2));
    }

    @Test
    public void theHoursAreGivenBasedOnTheWorkingHoursSpecifiedByTheCalendar() {
        LocalDate start = new LocalDate(2006, 10, 5);
        final int TASK_DURATION_DAYS = 1;
        final int halfWorkingDay = 4;
        givenBaseCalendarWithoutExceptions(halfWorkingDay);
        givenTaskWithStartAndEnd(toInterval(start, Period
                .days(TASK_DURATION_DAYS)));
        givenGenericResourceAllocationForTask(task);
        givenWorkersWithoutLoadAndWithoutCalendar();

        genericResourceAllocation.forResources(Arrays.asList(worker1))
                .allocate(ResourcePerDayUnit.amount(1));

        List<GenericDayAssigment> assigmments = genericResourceAllocation
                .getOrderedAssigmentsFor(worker1);
        assertThat(assigmments.get(0).getHours(), equalTo(halfWorkingDay));
    }

    @Test
    public void ifThereisNoTaskCalendarTheWorkingHoursAreSpecifiedbyTheDefaultWorkingDay() {
        LocalDate start = new LocalDate(2006, 10, 5);
        final int TASK_DURATION_DAYS = 1;
        final Integer defaultWorkableHours = SameWorkHoursEveryDay
                .getDefaultWorkingDay()
                .getWorkableHours(start);
        givenBaseCalendarWithoutExceptions(defaultWorkableHours);
        givenTaskWithStartAndEnd(toInterval(start, Period
                .days(TASK_DURATION_DAYS)));
        givenGenericResourceAllocationForTask(task);
        givenWorkersWithoutLoadAndWithoutCalendar();

        genericResourceAllocation.forResources(Arrays.asList(worker1))
                .allocate(ResourcePerDayUnit.amount(1));

        List<GenericDayAssigment> assigmments = genericResourceAllocation
                .getOrderedAssigmentsFor(worker1);
        assertThat(assigmments.get(0).getHours(), equalTo(defaultWorkableHours));
    }

    @Test
    public void moreBusyResourcesAreGivenLessLoad() {
        final int TASK_DURATION_DAYS = 4;
        givenBaseCalendarWithoutExceptions(8);
        LocalDate start = new LocalDate(2006, 10, 5);
        givenTaskWithStartAndEnd(toInterval(start, Period
                .days(TASK_DURATION_DAYS)));
        givenGenericResourceAllocationForTask(task);
        givenWorkersWithLoads(3, 12, 1);

        genericResourceAllocation.forResources(workers).allocate(
                ResourcePerDayUnit.amount(1));

        List<GenericDayAssigment> assigmentsWorker1 = genericResourceAllocation
                .getOrderedAssigmentsFor(worker1);
        assertThat(assigmentsWorker1, haveHours(3, 3, 3, 3));
        List<GenericDayAssigment> assigmentsWorker2 = genericResourceAllocation
                .getOrderedAssigmentsFor(worker2);
        assertThat(assigmentsWorker2, haveHours(0, 0, 0, 0));
        List<GenericDayAssigment> assigmentsWorker3 = genericResourceAllocation
                .getOrderedAssigmentsFor(worker3);
        assertThat(assigmentsWorker3, haveHours(5, 5, 5, 5));
    }

    private Matcher<List<GenericDayAssigment>> haveHours(final int... hours) {
        return new BaseMatcher<List<GenericDayAssigment>>() {

            @Override
            public boolean matches(Object value) {
                if (value instanceof List) {
                    List<GenericDayAssigment> assigments = (List<GenericDayAssigment>) value;
                    for (int i = 0; i < hours.length; i++) {
                        if (hours[i] != assigments.get(i).getHours()) {
                            return false;
                        }
                    }
                    return true;
                }
                return false;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("must have hours: "
                        + Arrays.toString(hours));
            }
        };
    }

    private static Interval toInterval(LocalDate start, Period period) {
        return new Interval(start.toDateTimeAtStartOfDay(), start.plus(period)
                .toDateTimeAtStartOfDay());
    }

}
