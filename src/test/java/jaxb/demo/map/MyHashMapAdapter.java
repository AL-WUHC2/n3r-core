package jaxb.demo.map;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.bind.annotation.adapters.XmlAdapter;

@SuppressWarnings("rawtypes")
public final class MyHashMapAdapter extends XmlAdapter<MyHashMapType, HashMap> {

    @SuppressWarnings("unchecked")
    @Override
    public MyHashMapType marshal(HashMap arg0) throws Exception {
        MyHashMapType myHashMapType = new MyHashMapType();
        for (Entry entry : (Set<Entry>) arg0.entrySet()) {
            MyHashEntryType myHashEntryType = new MyHashEntryType();
            myHashEntryType.key = (Integer) entry.getKey();
            myHashEntryType.value = (String) entry.getValue();
            myHashMapType.entries.add(myHashEntryType);
            // myHashMapType = myHashEntryType;
        }
        return myHashMapType;
    }

    @SuppressWarnings("unchecked")
    @Override
    public HashMap unmarshal(MyHashMapType arg0) throws Exception {
        HashMap hashMap = new HashMap();
        for (MyHashEntryType myHashEntryType : arg0.entries)
            hashMap.put(myHashEntryType.key, myHashEntryType.value);
        return hashMap;
    }

}
