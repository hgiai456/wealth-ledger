CREATE SCHEMA IF NOT EXISTS ledger;

CREATE TABLE ledger.accounts
(
    id              VARCHAR(36)    NOT NULL,
    user_id         VARCHAR(36)    NOT NULL,
    name            VARCHAR(150)   NOT NULL,
    type            VARCHAR(30)    NOT NULL,
    provider        VARCHAR(150)   NOT NULL,
    currency        CHAR(3)        NOT NULL,
    current_balance NUMERIC(19, 4) NOT NULL DEFAULT 0,
    allow_negative  BOOLEAN        NOT NULL DEFAULT FALSE,
    status          VARCHAR(20)    NOT NULL DEFAULT 'ACTIVE',
    version         BIGINT         NOT NULL DEFAULT 0,
    created_at      TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at      TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    closed_at       TIMESTAMP(6) WITH TIME ZONE,

    CONSTRAINT pk_accounts
        PRIMARY KEY (id),

    CONSTRAINT ck_accounts_type
        CHECK (type IN (
                        'CASH',
                        'BANK_ACCOUNT',
                        'E_WALLET',
                        'SAVINGS',
                        'INVESTMENT',
                        'LIABILITY'
            )),

    CONSTRAINT ck_accounts_currency
        CHECK (currency ~ '^[A-Z]{3}$'),

    CONSTRAINT ck_accounts_status
        CHECK (status IN ('ACTIVE', 'CLOSED')),

    CONSTRAINT ck_accounts_balance
        CHECK (allow_negative OR current_balance >= 0),

    CONSTRAINT ck_accounts_closed_at
        CHECK (
            (status = 'ACTIVE' AND closed_at IS NULL)
            OR
            (status = 'CLOSED' AND closed_at IS NOT NULL)
        )
);

CREATE UNIQUE INDEX uk_accounts_user_name
    ON ledger.accounts (user_id, lower(name));

CREATE INDEX idx_accounts_user_status
    ON ledger.accounts (user_id, status);


CREATE TABLE ledger.categories
(
    id         VARCHAR(36)  NOT NULL,
    user_id    VARCHAR(36),
    parent_id  VARCHAR(36),
    name       VARCHAR(120) NOT NULL,
    type       VARCHAR(20)  NOT NULL,
    active     BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,

    CONSTRAINT pk_categories
        PRIMARY KEY (id),

    CONSTRAINT fk_categories_parent
        FOREIGN KEY (parent_id)
            REFERENCES ledger.categories (id)
            ON DELETE RESTRICT,

    CONSTRAINT ck_categories_type
        CHECK (type IN ('INCOME', 'EXPENSE'))
);

CREATE UNIQUE INDEX uk_categories_user_type_name
    ON ledger.categories (user_id, type, lower(name))
    WHERE user_id IS NOT NULL;

CREATE UNIQUE INDEX uk_categories_system_type_name
    ON ledger.categories (type, lower(name))
    WHERE user_id IS NULL;

CREATE INDEX idx_categories_parent
    ON ledger.categories (parent_id);


CREATE TABLE ledger.financial_transactions
(
    id                         VARCHAR(36)    NOT NULL,
    user_id                    VARCHAR(36)    NOT NULL,
    type                       VARCHAR(30)    NOT NULL,
    amount                     NUMERIC(19, 4) NOT NULL,
    currency                   CHAR(3)        NOT NULL,
    status                     VARCHAR(20)    NOT NULL,
    source_type                VARCHAR(30)    NOT NULL,
    transaction_date           TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    description                VARCHAR(500),
    created_by_user_id         VARCHAR(36),
    created_by_type            VARCHAR(20)    NOT NULL,
    idempotency_key            VARCHAR(100)   NOT NULL,
    request_hash               CHAR(64),
    reversal_of_transaction_id VARCHAR(36),
    reversal_reason            VARCHAR(500),
    metadata                   JSONB,
    created_at                 TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    completed_at               TIMESTAMP(6) WITH TIME ZONE,

    CONSTRAINT pk_financial_transactions
        PRIMARY KEY (id),

    CONSTRAINT fk_transactions_reversal_of
        FOREIGN KEY (reversal_of_transaction_id)
            REFERENCES ledger.financial_transactions (id)
            ON DELETE RESTRICT,

    CONSTRAINT uk_transactions_user_idempotency
        UNIQUE (user_id, idempotency_key),

    CONSTRAINT uk_transactions_reversal_of
        UNIQUE (reversal_of_transaction_id),

    CONSTRAINT ck_transactions_amount
        CHECK (amount > 0),

    CONSTRAINT ck_transactions_currency
        CHECK (currency ~ '^[A-Z]{3}$'),

    CONSTRAINT ck_transactions_type
        CHECK (type IN (
            'OPENING_BALANCE',
            'INCOME',
            'EXPENSE',
            'TRANSFER',
            'SAVINGS_DEPOSIT',
            'SAVINGS_WITHDRAWAL',
            'STOCK_BUY',
            'STOCK_SELL',
            'INTEREST',
            'FEE',
            'ADJUSTMENT',
            'REVERSAL'
        )),

    CONSTRAINT ck_transactions_status
        CHECK (status IN (
            'PENDING',
            'COMPLETED',
            'FAILED',
            'REVERSED'
        )),

    CONSTRAINT ck_transactions_source
        CHECK (source_type IN (
            'MANUAL',
            'WEBHOOK',
            'IMPORT',
            'SYSTEM'
        )),

    CONSTRAINT ck_transactions_created_by_type
        CHECK (created_by_type IN (
            'USER',
            'WEBHOOK',
            'SYSTEM'
        )),

    CONSTRAINT ck_transactions_reversal
        CHECK (
            type <> 'REVERSAL'
            OR (
                reversal_of_transaction_id IS NOT NULL
                AND reversal_reason IS NOT NULL
            )
        )
);

CREATE INDEX idx_transactions_user_date
    ON ledger.financial_transactions
        (user_id, transaction_date DESC);

CREATE INDEX idx_transactions_user_type
    ON ledger.financial_transactions
        (user_id, type);


CREATE TABLE ledger.transaction_entries
(
    id             VARCHAR(36)    NOT NULL,
    transaction_id VARCHAR(36)    NOT NULL,
    account_id     VARCHAR(36)    NOT NULL,
    category_id    VARCHAR(36),
    direction      VARCHAR(10)    NOT NULL,
    amount         NUMERIC(19, 4) NOT NULL,
    balance_after  NUMERIC(19, 4) NOT NULL,
    description    VARCHAR(500),
    created_at     TIMESTAMP(6) WITH TIME ZONE NOT NULL,

    CONSTRAINT pk_transaction_entries
        PRIMARY KEY (id),

    CONSTRAINT fk_entries_transaction
        FOREIGN KEY (transaction_id)
            REFERENCES ledger.financial_transactions (id)
            ON DELETE RESTRICT,

    CONSTRAINT fk_entries_account
        FOREIGN KEY (account_id)
            REFERENCES ledger.accounts (id)
            ON DELETE RESTRICT,

    CONSTRAINT fk_entries_category
        FOREIGN KEY (category_id)
            REFERENCES ledger.categories (id)
            ON DELETE RESTRICT,

    CONSTRAINT ck_entries_direction
        CHECK (direction IN ('DEBIT', 'CREDIT')),

    CONSTRAINT ck_entries_amount
        CHECK (amount > 0)
);

CREATE INDEX idx_entries_account_created
    ON ledger.transaction_entries
        (account_id, created_at DESC);

CREATE INDEX idx_entries_transaction
    ON ledger.transaction_entries (transaction_id);

CREATE INDEX idx_entries_category
    ON ledger.transaction_entries (category_id);