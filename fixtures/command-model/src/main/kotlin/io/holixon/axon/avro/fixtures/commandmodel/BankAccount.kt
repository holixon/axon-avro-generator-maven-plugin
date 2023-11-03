package io.holixon.axon.avro.fixtures.commandmodel

import io.holixon.axon.avro.fixtures.schema.command.CreateBankAccount
import io.holixon.axon.avro.fixtures.schema.event.BankAccountCreated
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.spring.stereotype.Aggregate
import org.jmolecules.ddd.annotation.AggregateRoot
import java.math.BigDecimal
import java.util.*

@AggregateRoot
@Aggregate
class BankAccount() {
  companion object {

    @JvmStatic
    fun create(command: CreateBankAccount) {
      AggregateLifecycle.apply(
        BankAccountCreated.newBuilder()
          .setBankAccountId(command.bankAccountId)
          .setInitialBalance(command.initialBalance)
          .build()
      )
    }
  }

  @AggregateIdentifier
  lateinit var bankAccountId: UUID

  var balance: BigDecimal = BigDecimal.ZERO

  @EventSourcingHandler
  fun on(event: BankAccountCreated) {
    bankAccountId = event.bankAccountId
    balance = BigDecimal.ONE // TODOevent.initialBalance
  }
}
