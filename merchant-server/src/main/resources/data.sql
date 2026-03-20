INSERT INTO merchant_order (
    order_uid,
    product_name,
    amount,
    order_status,
    created_at,
    updated_at
) VALUES (
             'ORDER-1001',
             '텀블러',
             15000,
             'CREATED',
             NOW(),
             NOW()
         );