/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.weld.osgi.examples.web.england;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.weld.environment.osgi.api.annotation.Publish;
import org.jboss.weld.osgi.examples.web.api.Hotel;
import org.jboss.weld.osgi.examples.web.api.HotelProvider;

@Publish
@ApplicationScoped
public class EnglandHotelProvider implements HotelProvider {

    @Override
    public Collection<Hotel> hotels() {
        Collection<Hotel> hotels = new ArrayList<Hotel>();
        hotels.add(new Hotel("The Montcalm", "London", "England", "2222", new Double(100)));
        hotels.add(new Hotel("The Berkeley", "London", "England", "2222", new Double(200)));
        return hotels;
    }

    @Override
    public String getCountry() {
        return "England";
    }

    @Override
    public boolean book(String id, Date checkin, Date checkout, Integer beds,
            Boolean smocking, String cardNumber, String cardName,
            String cardMonth, String cardYear) {
        return true;
    }
}