# Honor mobile map package expiration date

Access the expiration information of an expired mobile map package.

![Image of honor mobile map package expiration date](HonorMobileMapPackageExpirationDate.png)

## Use case

The data contained within a mobile map package (MMPK) may only be relevant for a fixed period of time. Using ArcGIS Pro, the author of an MMPK can set an expiration date to ensure the user is aware the data is out of date.

As long as the author of an MMPK has set an expiration date, the expiration date can be read even if the MMPK has not yet expired. For example, developers could also use this API to warn app users that an MMPK may be expiring soon.

## How to use the sample

Run the app. The author of the MMPK used in this sample chose to set the MMPK's map as still readable, even if it's expired. The app presents expiration information to the user.

## How it works

1. Create a `MobileMapPackage` using the URI to a local .mmpk file and load it.
2. Use `mobileMapPackage.getExpiration()` to get the expiration information. Get the expiration message with `getMessage()` and the expiration date with `getDate()`.

## Relevant API

* Expiration
* MobileMapPackage

## Tags

expiration, mmpk
