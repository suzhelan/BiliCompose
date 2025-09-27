package top.suzhelan.bili.api

import io.ktor.client.plugins.ClientRequestException
import io.ktor.http.HttpStatusCode

class ApiException(val code: Int, override val message: String, override val cause: Throwable)
    : RuntimeException(message, cause) {
    companion object {
        fun build(e: Throwable): ApiException {
            val clientException = e as? ClientRequestException ?: return ApiException(
                code = -1,
                message = e.message ?: "未知异常",
                cause = e
            )
            val exceptionResponse = clientException.response
            return when (exceptionResponse.status) {
                HttpStatusCode.Continue -> ApiException(100, "Continue(继续)", e)
                HttpStatusCode.SwitchingProtocols -> ApiException(101, "Switching Protocols(切换协议)", e)
                HttpStatusCode.Processing -> ApiException(102, "Processing(处理中)", e)
                HttpStatusCode.OK -> ApiException(200, "OK(成功)", e)
                HttpStatusCode.Created -> ApiException(201, "Created(已创建)", e)
                HttpStatusCode.Accepted -> ApiException(202, "Accepted(已接受)", e)
                HttpStatusCode.NonAuthoritativeInformation -> ApiException(203, "Non-Authoritative Information(非权威信息)", e)
                HttpStatusCode.NoContent -> ApiException(204, "No Content(无内容)", e)
                HttpStatusCode.ResetContent -> ApiException(205, "Reset Content(重置内容)", e)
                HttpStatusCode.PartialContent -> ApiException(206, "Partial Content(部分内容)", e)
                HttpStatusCode.MultiStatus -> ApiException(207, "Multi-Status(多状态)", e)
                HttpStatusCode.MultipleChoices -> ApiException(300, "Multiple Choices(多种选择)", e)
                HttpStatusCode.MovedPermanently -> ApiException(301, "Moved Permanently(永久移动)", e)
                HttpStatusCode.Found -> ApiException(302, "Found(临时移动)", e)
                HttpStatusCode.SeeOther -> ApiException(303, "See Other(查看其他)", e)
                HttpStatusCode.NotModified -> ApiException(304, "Not Modified(未修改)", e)
                HttpStatusCode.UseProxy -> ApiException(305, "Use Proxy(使用代理)", e)
                HttpStatusCode.SwitchProxy -> ApiException(306, "Switch Proxy(切换代理)", e)
                HttpStatusCode.TemporaryRedirect -> ApiException(307, "Temporary Redirect(临时重定向)", e)
                HttpStatusCode.PermanentRedirect -> ApiException(308, "Permanent Redirect(永久重定向)", e)
                HttpStatusCode.BadRequest -> ApiException(400, "Bad Request(错误请求)", e)
                HttpStatusCode.Unauthorized -> ApiException(401, "Unauthorized(未授权)", e)
                HttpStatusCode.PaymentRequired -> ApiException(402, "Payment Required(需要付款)", e)
                HttpStatusCode.Forbidden -> ApiException(403, "Forbidden(禁止访问)", e)
                HttpStatusCode.NotFound -> ApiException(404, "Not Found(未找到)", e)
                HttpStatusCode.MethodNotAllowed -> ApiException(405, "Method Not Allowed(方法不允许)", e)
                HttpStatusCode.NotAcceptable -> ApiException(406, "Not Acceptable(不可接受)", e)
                HttpStatusCode.ProxyAuthenticationRequired -> ApiException(407, "Proxy Authentication Required(需要代理认证)", e)
                HttpStatusCode.RequestTimeout -> ApiException(408, "Request Timeout(请求超时)", e)
                HttpStatusCode.Conflict -> ApiException(409, "Conflict(冲突)", e)
                HttpStatusCode.Gone -> ApiException(410, "Gone(已删除)", e)
                HttpStatusCode.LengthRequired -> ApiException(411, "Length Required(需要长度)", e)
                HttpStatusCode.PreconditionFailed -> ApiException(412, "Precondition Failed(前提条件失败)", e)
                HttpStatusCode.PayloadTooLarge -> ApiException(413, "Payload Too Large(负载过大)", e)
                HttpStatusCode.RequestURITooLong -> ApiException(414, "Request-URI Too Long(请求URI过长)", e)
                HttpStatusCode.UnsupportedMediaType -> ApiException(415, "Unsupported Media Type(不支持的媒体类型)", e)
                HttpStatusCode.RequestedRangeNotSatisfiable -> ApiException(416, "Requested Range Not Satisfiable(请求范围不可满足)", e)
                HttpStatusCode.ExpectationFailed -> ApiException(417, "Expectation Failed(期望失败)", e)
                HttpStatusCode.UnprocessableEntity -> ApiException(422, "Unprocessable Entity(无法处理的实体)", e)
                HttpStatusCode.Locked -> ApiException(423, "Locked(已锁定)", e)
                HttpStatusCode.FailedDependency -> ApiException(424, "Failed Dependency(依赖失败)", e)
                HttpStatusCode.TooEarly -> ApiException(425, "Too Early(过早)", e)
                HttpStatusCode.UpgradeRequired -> ApiException(426, "Upgrade Required(需要升级)", e)
                HttpStatusCode.TooManyRequests -> ApiException(429, "Too Many Requests(请求过多)", e)
                HttpStatusCode.RequestHeaderFieldTooLarge -> ApiException(431, "Request Header Fields Too Large(请求头字段过大)", e)
                HttpStatusCode.InternalServerError -> ApiException(500, "Internal Server Error(内部服务器错误)", e)
                HttpStatusCode.NotImplemented -> ApiException(501, "Not Implemented(未实现)", e)
                HttpStatusCode.BadGateway -> ApiException(502, "Bad Gateway(错误的网关)", e)
                HttpStatusCode.ServiceUnavailable -> ApiException(503, "Service Unavailable(服务不可用)", e)
                HttpStatusCode.GatewayTimeout -> ApiException(504, "Gateway Timeout(网关超时)", e)
                HttpStatusCode.VersionNotSupported -> ApiException(505, "HTTP Version Not Supported(HTTP版本不支持)", e)
                HttpStatusCode.VariantAlsoNegotiates -> ApiException(506, "Variant Also Negotiates(变体也协商)", e)
                HttpStatusCode.InsufficientStorage -> ApiException(507, "Insufficient Storage(存储空间不足)", e)
                else -> ApiException(4000, "Unknown Error(未知错误)", e)
            }
        }
    }
    fun <T> toResponse(): BiliResponse<T> {
        return BiliResponse.Error(code = code, msg = message, cause = this)
    }
}