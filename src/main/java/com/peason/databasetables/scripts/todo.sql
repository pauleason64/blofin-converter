calculate net volume
------------------------
select sum(volume),pair from (
select pair,tradetype, case when   tradetype="sell" then -1 * volume else volume end as volume
from crypto_trans.trades
) x group by x.pair


