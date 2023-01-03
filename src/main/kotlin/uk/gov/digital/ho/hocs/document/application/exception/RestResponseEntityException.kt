package uk.gov.digital.ho.hocs.document.application.exception

import net.logstash.logback.argument.StructuredArguments
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import uk.gov.digital.ho.hocs.document.application.LogEvent

@ControllerAdvice
class RestResponseEntityExceptionHandler {

    companion object {
        val log: Logger = LoggerFactory.getLogger(this::class.java)
    }

//    @ExceptionHandler(EntityCreationException::class)
//    fun handle(e: EntityCreationException): ResponseEntity<*> {
//        log.error("EntityCreationException: {}", e.message, StructuredArguments.value(LogEvent.EVENT, e.event))
//        return ResponseEntity<Any?>(e.message, HttpStatus.INTERNAL_SERVER_ERROR)
//    }
//
//    @ExceptionHandler(EntityNotFoundException::class)
//    fun handle(e: EntityNotFoundException): ResponseEntity<*> {
//        log.error("EntityNotFoundException: {}", e.message, StructuredArguments.value(LogEvent.EVENT, e.event))
//        return ResponseEntity<Any?>(e.message, HttpStatus.NOT_FOUND)
//    }
//
//    @ExceptionHandler(S3Exception::class)
//    fun handle(e: S3Exception): ResponseEntity<*> {
//        log.error("S3Exception: {} caused by {}", e.message, e.cause?.message, StructuredArguments.value(LogEvent.EVENT, e.event))
//        return ResponseEntity<Any?>(e.message, HttpStatus.INTERNAL_SERVER_ERROR)
//    }
//
//    @ExceptionHandler(Exception::class)
//    fun handle(e: Exception): ResponseEntity<*> {
//        log.error("Exception: {}", e.message, StructuredArguments.value(LogEvent.EVENT, LogEvent.UNCAUGHT_EXCEPTION))
//        return ResponseEntity(e.message, HttpStatus.INTERNAL_SERVER_ERROR)
//    }
}