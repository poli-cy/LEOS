/*
 * Copyright 2019 European Commission
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *     https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */
package eu.europa.ec.leos.annotate.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Aspect
@Component
public class Profiler {
    private static final Logger LOG = LoggerFactory.getLogger(Profiler.class);

    @Around("execution(public * *.controllers.*.*(..))")
    public Object logMethod(final ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            final String mName = joinPoint.getSignature().toShortString();
            LOG.trace("{} started.", mName);
            final StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            final Object retVal = joinPoint.proceed();
            stopWatch.stop();
            LOG.trace("{} finished. Took {}ms", mName, stopWatch.getTotalTimeMillis());
            return retVal;
        } catch (final Exception ex) {
            LOG.error("Error occurred within running profiler aspect");
            throw ex;
        }
    }
}