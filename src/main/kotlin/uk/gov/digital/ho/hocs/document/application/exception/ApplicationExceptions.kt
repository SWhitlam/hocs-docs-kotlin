package uk.gov.digital.ho.hocs.document.application.exception

import uk.gov.digital.ho.hocs.document.application.LogEvent

class DocumentConversionException(message: String?, val event: LogEvent) : Exception(message)

class MalwareCheckException(message: String?, val event: LogEvent) : Exception(message)

class EntityCreationException(msg: String?, val event: LogEvent) : RuntimeException(msg)

class EntityNotFoundException(msg: String?, val event: LogEvent) : RuntimeException(msg)

class S3Exception(msg: String?, val event: LogEvent, cause: Exception?) : RuntimeException(msg, cause)

class ResourceException internal constructor(msg: String?, val event: LogEvent, vararg args: Any?) : RuntimeException(String.format(msg!!, *args))
