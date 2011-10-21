object Main extends App {
  import epcis._
  
  val xml = <ObjectEvent
      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      xmlns:epcis="urn:epcglobal:epcis:xsd:1"
      xsi:type="epcis:ObjectEventType">
    <eventTime>2010-01-01</eventTime>
    <eventTimeZoneOffset>Z</eventTimeZoneOffset>
    <epcList/>
    <action>ADD</action>
  </ObjectEvent>
  
  val normalizedEvent: EPCISEventType = scalaxb.fromXML[EPCISEventType](xml)
  println(normalizedEvent.toString)
  
  val temp = scalaxb.toXML(normalizedEvent, None, "ObjectType", defaultScope)
  println(temp)
}
