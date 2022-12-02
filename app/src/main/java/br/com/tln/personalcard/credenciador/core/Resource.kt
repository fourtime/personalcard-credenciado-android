package br.com.tln.personalcard.credenciador.core

open class Resource<T>(
    val status: Status,
    val data: T?,
    val message: String?
) {

    fun copy(
        status: Status = this.status,
        data: T? = this.data,
        message: String? = this.message
    ): Resource<T> {
        return Resource(
            status = status,
            data = data,
            message = message
        )
    }

    enum class Status {
        SUCCESS, ERROR, LOADING
    }

    companion object {
        fun <T> success(data: T? = null, message: String? = null): Resource<T> {
            return Resource(
                status = Status.SUCCESS,
                data = data,
                message = message
            )
        }

        fun <T> error(data: T? = null, message: String? = null): Resource<T> {
            return Resource(
                status = Status.ERROR,
                data = data,
                message = message
            )
        }

        fun <T> loading(data: T? = null, message: String? = null): Resource<T> {
            return Resource(
                status = Status.LOADING,
                data = data,
                message = message
            )
        }
    }
}