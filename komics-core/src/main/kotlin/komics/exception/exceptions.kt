package komics.exception

class DataFormatException : RuntimeException {
    constructor() : super() {
    }

    constructor(message: String) : super(message) {
    }

    constructor(throwable: Throwable) : super(throwable) {
    }

    constructor(message: String, throwable: Throwable) {
    }
}