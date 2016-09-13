package komics.exception

class DataFormatException : RuntimeException {
    constructor(message: String) : super(message) {
    }
}

class ConfigException : RuntimeException {
    constructor(message: String) : super(message) {
    }

    constructor(e: Exception) : super(e) {
    }
}