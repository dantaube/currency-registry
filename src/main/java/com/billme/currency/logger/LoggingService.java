package com.billme.currency.logger;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoggingService {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingService.class);

    private LogEventDao         dao;

    private DateTimeFormatter   formatter;

    public LoggingService(@Autowired LogEventDao dao, @Autowired DateTimeFormatter formatter) {
        this.dao = dao;
        this.formatter = formatter;
    }

    public void logEvent(String currencyCode, String clientAddress) {
        LogEvent logEvent = new LogEvent(currencyCode, clientAddress);
        LOG.info("New client request: {}", logEvent);
        dao.save(logEvent);
    }

    public List<LogEventDto> getLogEvents() {
        return dao.findAllByOrderByIdDesc().stream().map(logEvent -> toDto(logEvent)).collect(Collectors.toList());
    }

    public void clean() {
        dao.deleteAll();
    }

    private LogEventDto toDto(LogEvent logEvent) {
        Instant dateTime = Instant.ofEpochMilli(logEvent.getTimestamp());
        return new LogEventDto(formatter.format(dateTime), logEvent.getCurrencyCode(), logEvent.getClientAddress());
    }

}
