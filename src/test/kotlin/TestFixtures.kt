package io.holixon.avro.maven

import io.toolisticon.maven.io.FileExt.subFolder
import java.io.File
import java.io.PrintWriter

object TestFixtures {

  fun subPath(root:File, file:File) = file.path.removePrefix(root.path + "/")

  fun createFqnPath(root: File, fqn: String): File = fqn.split(".").fold(root) { file, part -> file.subFolder(part) }

  fun createJavaFile(target:File, fqn: String, code:String): File {
    val tmpFqn = fqn.removeSuffix(".java")

    val classFile = tmpFqn.substringAfterLast(".") + ".java"
    val packagePath = tmpFqn.substringBeforeLast(".")

    val directory = createFqnPath(target, packagePath)

    val file = File("${directory.path}/$classFile")

    PrintWriter(file).use {
      it.write(TestFixtures.generatedBankAccountCreatedEvent_java)
    }

    return file
  }



  val balanceChangedEventAvsc: String = """
    {
      "type": "record",
      "namespace": "io.holixon.schema.bank.event",
      "name": "BalanceChangedEvent",
      "doc": "Domain event containing accountId and new balance",
      "meta": {
        "type": "event",
        "revision": "1"
      },
      "fields": [
        {
          "name": "accountId",
            "type": "string"
        },
        {
          "name": "newBalance",
          "type": "int"
        }
      ]
    }
  """.trimIndent()

  val balanceChangedEventNotParsableDueToExtraCommaAvsc: String = """
    {
      "type": "record",
      "namespace": "io.holixon.schema.bank.event",
      "name": "BalanceChangedEvent",
      "doc": "Domain event containing accountId and new balance",
      "meta": {
        "type": "event",
        "revision": "1"
      },
      "fields": [
        {
          "name": "accountId",
            "type": "string"
        },
        {
          "name": "newBalance",
          "type": "int",
        }
      ]
    }
  """.trimIndent()

  val generatedBankAccountCreatedEvent_java = """
    /**
     * Autogenerated by Avro
     *
     * DO NOT EDIT DIRECTLY
     */
    package io.holixon.schema.bank.event;

    import org.apache.avro.generic.GenericArray;
    import org.apache.avro.specific.SpecificData;
    import org.apache.avro.util.Utf8;
    import org.apache.avro.message.BinaryMessageEncoder;
    import org.apache.avro.message.BinaryMessageDecoder;
    import org.apache.avro.message.SchemaStore;

    /** A bank account has been created */
    @org.apache.avro.specific.AvroGenerated
    public class BankAccountCreatedEvent extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
      private static final long serialVersionUID = 4157939516342518315L;


      public static final org.apache.avro.Schema SCHEMA${'$'} = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"BankAccountCreatedEvent\",\"namespace\":\"io.holixon.schema.bank.event\",\"doc\":\"A bank account has been created\",\"fields\":[{\"name\":\"accountId\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"initialBalance\",\"type\":\"int\"},{\"name\":\"maximalBalance\",\"type\":\"int\"}],\"meta\":{\"type\":\"event\",\"revision\":\"1\"}}");
      public static org.apache.avro.Schema getClassSchema() { return SCHEMA${'$'}; }

      private static final SpecificData MODEL${'$'} = new SpecificData();

      private static final BinaryMessageEncoder<BankAccountCreatedEvent> ENCODER =
          new BinaryMessageEncoder<BankAccountCreatedEvent>(MODEL${'$'}, SCHEMA${'$'});

      private static final BinaryMessageDecoder<BankAccountCreatedEvent> DECODER =
          new BinaryMessageDecoder<BankAccountCreatedEvent>(MODEL${'$'}, SCHEMA${'$'});

      /**
       * Return the BinaryMessageEncoder instance used by this class.
       * @return the message encoder used by this class
       */
      public static BinaryMessageEncoder<BankAccountCreatedEvent> getEncoder() {
        return ENCODER;
      }

      /**
       * Return the BinaryMessageDecoder instance used by this class.
       * @return the message decoder used by this class
       */
      public static BinaryMessageDecoder<BankAccountCreatedEvent> getDecoder() {
        return DECODER;
      }

      /**
       * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
       * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
       * @return a BinaryMessageDecoder instance for this class backed by the given SchemaStore
       */
      public static BinaryMessageDecoder<BankAccountCreatedEvent> createDecoder(SchemaStore resolver) {
        return new BinaryMessageDecoder<BankAccountCreatedEvent>(MODEL${'$'}, SCHEMA${'$'}, resolver);
      }

      /**
       * Serializes this BankAccountCreatedEvent to a ByteBuffer.
       * @return a buffer holding the serialized data for this instance
       * @throws java.io.IOException if this instance could not be serialized
       */
      public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
        return ENCODER.encode(this);
      }

      /**
       * Deserializes a BankAccountCreatedEvent from a ByteBuffer.
       * @param b a byte buffer holding serialized data for an instance of this class
       * @return a BankAccountCreatedEvent instance decoded from the given buffer
       * @throws java.io.IOException if the given bytes could not be deserialized into an instance of this class
       */
      public static BankAccountCreatedEvent fromByteBuffer(
          java.nio.ByteBuffer b) throws java.io.IOException {
        return DECODER.decode(b);
      }

      private java.lang.String accountId;
      private int initialBalance;
      private int maximalBalance;

      /**
       * Default constructor.  Note that this does not initialize fields
       * to their default values from the schema.  If that is desired then
       * one should use <code>newBuilder()</code>.
       */
      public BankAccountCreatedEvent() {}

      /**
       * All-args constructor.
       * @param accountId The new value for accountId
       * @param initialBalance The new value for initialBalance
       * @param maximalBalance The new value for maximalBalance
       */
      public BankAccountCreatedEvent(java.lang.String accountId, java.lang.Integer initialBalance, java.lang.Integer maximalBalance) {
        this.accountId = accountId;
        this.initialBalance = initialBalance;
        this.maximalBalance = maximalBalance;
      }

      public org.apache.avro.specific.SpecificData getSpecificData() { return MODEL${'$'}; }
      public org.apache.avro.Schema getSchema() { return SCHEMA${'$'}; }
      // Used by DatumWriter.  Applications should not call.
      public java.lang.Object get(int field${'$'}) {
        switch (field${'$'}) {
        case 0: return accountId;
        case 1: return initialBalance;
        case 2: return maximalBalance;
        default: throw new IndexOutOfBoundsException("Invalid index: " + field${'$'});
        }
      }

      // Used by DatumReader.  Applications should not call.
      @SuppressWarnings(value="unchecked")
      public void put(int field${'$'}, java.lang.Object value${'$'}) {
        switch (field${'$'}) {
        case 0: accountId = value${'$'} != null ? value${'$'}.toString() : null; break;
        case 1: initialBalance = (java.lang.Integer)value${'$'}; break;
        case 2: maximalBalance = (java.lang.Integer)value${'$'}; break;
        default: throw new IndexOutOfBoundsException("Invalid index: " + field${'$'});
        }
      }

      /**
       * Gets the value of the 'accountId' field.
       * @return The value of the 'accountId' field.
       */
      public java.lang.String getAccountId() {
        return accountId;
      }



      /**
       * Gets the value of the 'initialBalance' field.
       * @return The value of the 'initialBalance' field.
       */
      public int getInitialBalance() {
        return initialBalance;
      }



      /**
       * Gets the value of the 'maximalBalance' field.
       * @return The value of the 'maximalBalance' field.
       */
      public int getMaximalBalance() {
        return maximalBalance;
      }



      /**
       * Creates a new BankAccountCreatedEvent RecordBuilder.
       * @return A new BankAccountCreatedEvent RecordBuilder
       */
      public static io.holixon.schema.bank.event.BankAccountCreatedEvent.Builder newBuilder() {
        return new io.holixon.schema.bank.event.BankAccountCreatedEvent.Builder();
      }

      /**
       * Creates a new BankAccountCreatedEvent RecordBuilder by copying an existing Builder.
       * @param other The existing builder to copy.
       * @return A new BankAccountCreatedEvent RecordBuilder
       */
      public static io.holixon.schema.bank.event.BankAccountCreatedEvent.Builder newBuilder(io.holixon.schema.bank.event.BankAccountCreatedEvent.Builder other) {
        if (other == null) {
          return new io.holixon.schema.bank.event.BankAccountCreatedEvent.Builder();
        } else {
          return new io.holixon.schema.bank.event.BankAccountCreatedEvent.Builder(other);
        }
      }

      /**
       * Creates a new BankAccountCreatedEvent RecordBuilder by copying an existing BankAccountCreatedEvent instance.
       * @param other The existing instance to copy.
       * @return A new BankAccountCreatedEvent RecordBuilder
       */
      public static io.holixon.schema.bank.event.BankAccountCreatedEvent.Builder newBuilder(io.holixon.schema.bank.event.BankAccountCreatedEvent other) {
        if (other == null) {
          return new io.holixon.schema.bank.event.BankAccountCreatedEvent.Builder();
        } else {
          return new io.holixon.schema.bank.event.BankAccountCreatedEvent.Builder(other);
        }
      }

      /**
       * RecordBuilder for BankAccountCreatedEvent instances.
       */
      @org.apache.avro.specific.AvroGenerated
      public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<BankAccountCreatedEvent>
        implements org.apache.avro.data.RecordBuilder<BankAccountCreatedEvent> {

        private java.lang.String accountId;
        private int initialBalance;
        private int maximalBalance;

        /** Creates a new Builder */
        private Builder() {
          super(SCHEMA${'$'}, MODEL${'$'});
        }

        /**
         * Creates a Builder by copying an existing Builder.
         * @param other The existing Builder to copy.
         */
        private Builder(io.holixon.schema.bank.event.BankAccountCreatedEvent.Builder other) {
          super(other);
          if (isValidValue(fields()[0], other.accountId)) {
            this.accountId = data().deepCopy(fields()[0].schema(), other.accountId);
            fieldSetFlags()[0] = other.fieldSetFlags()[0];
          }
          if (isValidValue(fields()[1], other.initialBalance)) {
            this.initialBalance = data().deepCopy(fields()[1].schema(), other.initialBalance);
            fieldSetFlags()[1] = other.fieldSetFlags()[1];
          }
          if (isValidValue(fields()[2], other.maximalBalance)) {
            this.maximalBalance = data().deepCopy(fields()[2].schema(), other.maximalBalance);
            fieldSetFlags()[2] = other.fieldSetFlags()[2];
          }
        }

        /**
         * Creates a Builder by copying an existing BankAccountCreatedEvent instance
         * @param other The existing instance to copy.
         */
        private Builder(io.holixon.schema.bank.event.BankAccountCreatedEvent other) {
          super(SCHEMA${'$'}, MODEL${'$'});
          if (isValidValue(fields()[0], other.accountId)) {
            this.accountId = data().deepCopy(fields()[0].schema(), other.accountId);
            fieldSetFlags()[0] = true;
          }
          if (isValidValue(fields()[1], other.initialBalance)) {
            this.initialBalance = data().deepCopy(fields()[1].schema(), other.initialBalance);
            fieldSetFlags()[1] = true;
          }
          if (isValidValue(fields()[2], other.maximalBalance)) {
            this.maximalBalance = data().deepCopy(fields()[2].schema(), other.maximalBalance);
            fieldSetFlags()[2] = true;
          }
        }

        /**
          * Gets the value of the 'accountId' field.
          * @return The value.
          */
        public java.lang.String getAccountId() {
          return accountId;
        }


        /**
          * Sets the value of the 'accountId' field.
          * @param value The value of 'accountId'.
          * @return This builder.
          */
        public io.holixon.schema.bank.event.BankAccountCreatedEvent.Builder setAccountId(java.lang.String value) {
          validate(fields()[0], value);
          this.accountId = value;
          fieldSetFlags()[0] = true;
          return this;
        }

        /**
          * Checks whether the 'accountId' field has been set.
          * @return True if the 'accountId' field has been set, false otherwise.
          */
        public boolean hasAccountId() {
          return fieldSetFlags()[0];
        }


        /**
          * Clears the value of the 'accountId' field.
          * @return This builder.
          */
        public io.holixon.schema.bank.event.BankAccountCreatedEvent.Builder clearAccountId() {
          accountId = null;
          fieldSetFlags()[0] = false;
          return this;
        }

        /**
          * Gets the value of the 'initialBalance' field.
          * @return The value.
          */
        public int getInitialBalance() {
          return initialBalance;
        }


        /**
          * Sets the value of the 'initialBalance' field.
          * @param value The value of 'initialBalance'.
          * @return This builder.
          */
        public io.holixon.schema.bank.event.BankAccountCreatedEvent.Builder setInitialBalance(int value) {
          validate(fields()[1], value);
          this.initialBalance = value;
          fieldSetFlags()[1] = true;
          return this;
        }

        /**
          * Checks whether the 'initialBalance' field has been set.
          * @return True if the 'initialBalance' field has been set, false otherwise.
          */
        public boolean hasInitialBalance() {
          return fieldSetFlags()[1];
        }


        /**
          * Clears the value of the 'initialBalance' field.
          * @return This builder.
          */
        public io.holixon.schema.bank.event.BankAccountCreatedEvent.Builder clearInitialBalance() {
          fieldSetFlags()[1] = false;
          return this;
        }

        /**
          * Gets the value of the 'maximalBalance' field.
          * @return The value.
          */
        public int getMaximalBalance() {
          return maximalBalance;
        }


        /**
          * Sets the value of the 'maximalBalance' field.
          * @param value The value of 'maximalBalance'.
          * @return This builder.
          */
        public io.holixon.schema.bank.event.BankAccountCreatedEvent.Builder setMaximalBalance(int value) {
          validate(fields()[2], value);
          this.maximalBalance = value;
          fieldSetFlags()[2] = true;
          return this;
        }

        /**
          * Checks whether the 'maximalBalance' field has been set.
          * @return True if the 'maximalBalance' field has been set, false otherwise.
          */
        public boolean hasMaximalBalance() {
          return fieldSetFlags()[2];
        }


        /**
          * Clears the value of the 'maximalBalance' field.
          * @return This builder.
          */
        public io.holixon.schema.bank.event.BankAccountCreatedEvent.Builder clearMaximalBalance() {
          fieldSetFlags()[2] = false;
          return this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public BankAccountCreatedEvent build() {
          try {
            BankAccountCreatedEvent record = new BankAccountCreatedEvent();
            record.accountId = fieldSetFlags()[0] ? this.accountId : (java.lang.String) defaultValue(fields()[0]);
            record.initialBalance = fieldSetFlags()[1] ? this.initialBalance : (java.lang.Integer) defaultValue(fields()[1]);
            record.maximalBalance = fieldSetFlags()[2] ? this.maximalBalance : (java.lang.Integer) defaultValue(fields()[2]);
            return record;
          } catch (org.apache.avro.AvroMissingFieldException e) {
            throw e;
          } catch (java.lang.Exception e) {
            throw new org.apache.avro.AvroRuntimeException(e);
          }
        }
      }

      @SuppressWarnings("unchecked")
      private static final org.apache.avro.io.DatumWriter<BankAccountCreatedEvent>
        WRITER${'$'} = (org.apache.avro.io.DatumWriter<BankAccountCreatedEvent>)MODEL${'$'}.createDatumWriter(SCHEMA${'$'});

      @Override public void writeExternal(java.io.ObjectOutput out)
        throws java.io.IOException {
        WRITER${'$'}.write(this, SpecificData.getEncoder(out));
      }

      @SuppressWarnings("unchecked")
      private static final org.apache.avro.io.DatumReader<BankAccountCreatedEvent>
        READER${'$'} = (org.apache.avro.io.DatumReader<BankAccountCreatedEvent>)MODEL${'$'}.createDatumReader(SCHEMA${'$'});

      @Override public void readExternal(java.io.ObjectInput in)
        throws java.io.IOException {
        READER${'$'}.read(this, SpecificData.getDecoder(in));
      }

      @Override protected boolean hasCustomCoders() { return true; }

      @Override public void customEncode(org.apache.avro.io.Encoder out)
        throws java.io.IOException
      {
        out.writeString(this.accountId);

        out.writeInt(this.initialBalance);

        out.writeInt(this.maximalBalance);

      }

      @Override public void customDecode(org.apache.avro.io.ResolvingDecoder in)
        throws java.io.IOException
      {
        org.apache.avro.Schema.Field[] fieldOrder = in.readFieldOrderIfDiff();
        if (fieldOrder == null) {
          this.accountId = in.readString();

          this.initialBalance = in.readInt();

          this.maximalBalance = in.readInt();

        } else {
          for (int i = 0; i < 3; i++) {
            switch (fieldOrder[i].pos()) {
            case 0:
              this.accountId = in.readString();
              break;

            case 1:
              this.initialBalance = in.readInt();
              break;

            case 2:
              this.maximalBalance = in.readInt();
              break;

            default:
              throw new java.io.IOException("Corrupt ResolvingDecoder.");
            }
          }
        }
      }
    }
  """.trimIndent()
}
