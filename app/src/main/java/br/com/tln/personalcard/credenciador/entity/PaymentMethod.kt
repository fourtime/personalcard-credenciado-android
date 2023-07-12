package br.com.tln.personalcard.credenciador.entity

enum class PaymentMethod(val id: Int?) {
    PRE_PAID_CARD(1),
    POST_PAID_CARD(0),
    FLEET_CARD(2);

    companion object {
        fun fromId(id: Int?): PaymentMethod? {
            return values().firstOrNull {
                it.id == id
            }
        }
    }
}