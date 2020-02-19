/*
 * The MIT License (MIT)
 *
 * Copyright © 2013-2020 Florian Barras <https://barras.io> (florian@barras.io)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package jupiter.execution.scheduler;

import java.util.TimeZone;

import jupiter.common.util.Objects;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.listeners.JobChainingJobListener;

public class Schedulers {

	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS
	////////////////////////////////////////////////////////////////////////////////////////////////

	public static volatile String CHAIN_PREFIX = "CHAIN_";
	public static volatile String TRIGGER_PREFIX = "TRIGGER_";


	////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents the construction of {@link Schedulers}.
	 */
	protected Schedulers() {
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	// GENERATORS
	////////////////////////////////////////////////////////////////////////////////////////////////

	@SuppressWarnings("unchecked")
	public static Scheduler createScheduler(final String group, final String cronExpression,
			final String timeZone, final Class<? extends Job>... jobClasses)
			throws SchedulerException {
		final Scheduler scheduler = new StdSchedulerFactory().getScheduler();
		// Create the job details
		final JobDetail[] jobDetails = new JobDetail[jobClasses.length];
		jobDetails[0] = createJobDetail(group, jobClasses[0]);
		scheduler.scheduleJob(jobDetails[0], createTrigger(group, cronExpression, timeZone));
		for (int i = 1; i < jobClasses.length; ++i) {
			jobDetails[i] = createJobDetail(group, jobClasses[i]);
			scheduler.addJob(jobDetails[i], true);
		}
		// Chain the jobs
		if (jobDetails.length > 1) {
			scheduler.getListenerManager().addJobListener(createJobChain(group, jobDetails));
		}
		return scheduler;
	}

	public static JobDetail createJobDetail(final String group,
			final Class<? extends Job> jobClass) {
		return JobBuilder.newJob(jobClass)
				.withIdentity(group.concat(".").concat(Objects.getName(jobClass)), group)
				.storeDurably(true)
				.build();
	}

	public static CronTrigger createTrigger(final String group, final String cronExpression,
			final String timeZone) {
		return TriggerBuilder.newTrigger().withIdentity(TRIGGER_PREFIX.concat(group), group)
				.withSchedule(CronScheduleBuilder.cronSchedule(cronExpression)
						.inTimeZone(TimeZone.getTimeZone(timeZone)))
				.build();
	}

	public static JobChainingJobListener createJobChain(final String group,
			final JobDetail[] jobDetails) {
		final JobChainingJobListener jobChain = new JobChainingJobListener(
				CHAIN_PREFIX.concat(group));
		for (int i = 1; i < jobDetails.length; ++i) {
			jobChain.addJobChainLink(jobDetails[i - 1].getKey(), jobDetails[i].getKey());
		}
		return jobChain;
	}
}
