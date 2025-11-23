package com.example.service;

import com.example.exception.ServiceException;

public interface ExchangeRatesService {

    String getUSDExchangeRate() throws ServiceException;

    String getEURExchangeRate() throws ServiceException;

}
