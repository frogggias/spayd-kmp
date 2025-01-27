package io.stepuplabs.spaydkmp.common

/*
Representation of multiple Accounts
 */
data class AccountList(
    val accounts: List<Account>
) {
    override fun toString(): String {
        val builder = StringBuilder()
        for (account in accounts) {
            if (builder.isNotEmpty()) {
                builder.append(",")
            }

            builder.append(account.toString())
        }

        return builder.toString()
    }
}
