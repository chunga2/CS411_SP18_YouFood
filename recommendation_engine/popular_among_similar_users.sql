SELECT
  restaurant_name,
  restaurant_address,
  COUNT(*) AS amt
FROM (
       SELECT useremail AS otheremail
       FROM "Transaction"
         INNER JOIN (
                      SELECT
                        restaurant_name    AS my_name,
                        restaurant_address AS my_address
                      FROM "Transaction"
                      WHERE useremail = 'test0@illinois.net'
                    ) AS MyTable ON restaurant_address = my_address AND restaurant_name = my_name
     ) AS OtherRestaurants, "Transaction"
WHERE useremail <> 'test0@illinois.net'
GROUP BY restaurant_name, restaurant_address
ORDER BY amt DESC
LIMIT 10;