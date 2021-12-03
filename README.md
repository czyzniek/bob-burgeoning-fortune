# Crypto calculator for Bob's burgeoning fortune

## Overview

This program prints the value of Bob's crypto.

### Get actual exchange rate

For actual crypto's compare values is used [Crypto compare API].

####  Example request
```
curl 'https://min-api.cryptocompare.com/data/price?fsym=BTC&tsyms=EUR'
```

####  Example response

```json
{
  "EUR": 47827.57
}
```

### Run tests
```shell
./mvnw clean verify
```

### Run the program

- run the main method in BobBurgeoningFortuneApp
- run the command: `./mvnw clean package exec:java`
- run packed jar from target directory:
```shell
java -jar bob-burgeoning-fortune-1.0.jar <PATH_TO_CRYPTO_FILE> <COMPARE_CURRENCY>
```

Default values are: 
```
PATH_TO_CRYPTO_FILE: classpath://bobs_crypto.txt
COMPARE_CURRENCY: EUR
```
### Sample output

```
1 BTC -> 1 EUR = 50286.300000 | Total amount: 502863.000000 EUR
1 ETH -> 1 EUR = 4032.840000 | Total amount: 20164.200000 EUR
1 XRP -> 1 EUR = 0.862100 | Total amount: 1724.200000 EUR
Total Bob's crypto value is: 524751.400000 EUR
```

[Crypto compare API]: https://min-api.cryptocompare.com/documentation