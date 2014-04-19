/**
 * Autogenerated by Thrift Compiler (0.9.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package backtype.storm.generated;

import org.apache.thrift.scheme.IScheme;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.StandardScheme;

import org.apache.thrift.scheme.TupleScheme;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.EncodingUtils;
import org.apache.thrift.TException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.EnumMap;
import java.util.Set;
import java.util.HashSet;
import java.util.EnumSet;
import java.util.Collections;
import java.util.BitSet;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComponentCommon implements org.apache.thrift.TBase<ComponentCommon, ComponentCommon._Fields>, java.io.Serializable, Cloneable {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("ComponentCommon");

  private static final org.apache.thrift.protocol.TField INPUTS_FIELD_DESC = new org.apache.thrift.protocol.TField("inputs", org.apache.thrift.protocol.TType.MAP, (short)1);
  private static final org.apache.thrift.protocol.TField STREAMS_FIELD_DESC = new org.apache.thrift.protocol.TField("streams", org.apache.thrift.protocol.TType.MAP, (short)2);
  private static final org.apache.thrift.protocol.TField PARALLELISM_HINT_FIELD_DESC = new org.apache.thrift.protocol.TField("parallelism_hint", org.apache.thrift.protocol.TType.I32, (short)3);
  private static final org.apache.thrift.protocol.TField JSON_CONF_FIELD_DESC = new org.apache.thrift.protocol.TField("json_conf", org.apache.thrift.protocol.TType.STRING, (short)4);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new ComponentCommonStandardSchemeFactory());
    schemes.put(TupleScheme.class, new ComponentCommonTupleSchemeFactory());
  }

  public Map<GlobalStreamId,Grouping> inputs; // required
  public Map<String,StreamInfo> streams; // required
  public int parallelism_hint; // optional
  public String json_conf; // optional

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    INPUTS((short)1, "inputs"),
    STREAMS((short)2, "streams"),
    PARALLELISM_HINT((short)3, "parallelism_hint"),
    JSON_CONF((short)4, "json_conf");

    private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

    static {
      for (_Fields field : EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // INPUTS
          return INPUTS;
        case 2: // STREAMS
          return STREAMS;
        case 3: // PARALLELISM_HINT
          return PARALLELISM_HINT;
        case 4: // JSON_CONF
          return JSON_CONF;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    public static _Fields findByName(String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final String _fieldName;

    _Fields(short thriftId, String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  private static final int __PARALLELISM_HINT_ISSET_ID = 0;
  private byte __isset_bitfield = 0;
  private _Fields optionals[] = {_Fields.PARALLELISM_HINT,_Fields.JSON_CONF};
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.INPUTS, new org.apache.thrift.meta_data.FieldMetaData("inputs", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.MapMetaData(org.apache.thrift.protocol.TType.MAP, 
            new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, GlobalStreamId.class), 
            new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, Grouping.class))));
    tmpMap.put(_Fields.STREAMS, new org.apache.thrift.meta_data.FieldMetaData("streams", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.MapMetaData(org.apache.thrift.protocol.TType.MAP, 
            new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING), 
            new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, StreamInfo.class))));
    tmpMap.put(_Fields.PARALLELISM_HINT, new org.apache.thrift.meta_data.FieldMetaData("parallelism_hint", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.JSON_CONF, new org.apache.thrift.meta_data.FieldMetaData("json_conf", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(ComponentCommon.class, metaDataMap);
  }

  public ComponentCommon() {
  }

  public ComponentCommon(
    Map<GlobalStreamId,Grouping> inputs,
    Map<String,StreamInfo> streams)
  {
    this();
    this.inputs = inputs;
    this.streams = streams;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public ComponentCommon(ComponentCommon other) {
    __isset_bitfield = other.__isset_bitfield;
    if (other.isSetInputs()) {
      Map<GlobalStreamId,Grouping> __this__inputs = new HashMap<GlobalStreamId,Grouping>();
      for (Map.Entry<GlobalStreamId, Grouping> other_element : other.inputs.entrySet()) {

        GlobalStreamId other_element_key = other_element.getKey();
        Grouping other_element_value = other_element.getValue();

        GlobalStreamId __this__inputs_copy_key = new GlobalStreamId(other_element_key);

        Grouping __this__inputs_copy_value = new Grouping(other_element_value);

        __this__inputs.put(__this__inputs_copy_key, __this__inputs_copy_value);
      }
      this.inputs = __this__inputs;
    }
    if (other.isSetStreams()) {
      Map<String,StreamInfo> __this__streams = new HashMap<String,StreamInfo>();
      for (Map.Entry<String, StreamInfo> other_element : other.streams.entrySet()) {

        String other_element_key = other_element.getKey();
        StreamInfo other_element_value = other_element.getValue();

        String __this__streams_copy_key = other_element_key;

        StreamInfo __this__streams_copy_value = new StreamInfo(other_element_value);

        __this__streams.put(__this__streams_copy_key, __this__streams_copy_value);
      }
      this.streams = __this__streams;
    }
    this.parallelism_hint = other.parallelism_hint;
    if (other.isSetJson_conf()) {
      this.json_conf = other.json_conf;
    }
  }

  public ComponentCommon deepCopy() {
    return new ComponentCommon(this);
  }

  @Override
  public void clear() {
    this.inputs = null;
    this.streams = null;
    setParallelism_hintIsSet(false);
    this.parallelism_hint = 0;
    this.json_conf = null;
  }

  public int getInputsSize() {
    return (this.inputs == null) ? 0 : this.inputs.size();
  }

  public void putToInputs(GlobalStreamId key, Grouping val) {
    if (this.inputs == null) {
      this.inputs = new HashMap<GlobalStreamId,Grouping>();
    }
    this.inputs.put(key, val);
  }

  public Map<GlobalStreamId,Grouping> getInputs() {
    return this.inputs;
  }

  public ComponentCommon setInputs(Map<GlobalStreamId,Grouping> inputs) {
    this.inputs = inputs;
    return this;
  }

  public void unsetInputs() {
    this.inputs = null;
  }

  /** Returns true if field inputs is set (has been assigned a value) and false otherwise */
  public boolean isSetInputs() {
    return this.inputs != null;
  }

  public void setInputsIsSet(boolean value) {
    if (!value) {
      this.inputs = null;
    }
  }

  public int getStreamsSize() {
    return (this.streams == null) ? 0 : this.streams.size();
  }

  public void putToStreams(String key, StreamInfo val) {
    if (this.streams == null) {
      this.streams = new HashMap<String,StreamInfo>();
    }
    this.streams.put(key, val);
  }

  public Map<String,StreamInfo> getStreams() {
    return this.streams;
  }

  public ComponentCommon setStreams(Map<String,StreamInfo> streams) {
    this.streams = streams;
    return this;
  }

  public void unsetStreams() {
    this.streams = null;
  }

  /** Returns true if field streams is set (has been assigned a value) and false otherwise */
  public boolean isSetStreams() {
    return this.streams != null;
  }

  public void setStreamsIsSet(boolean value) {
    if (!value) {
      this.streams = null;
    }
  }

  public int getParallelism_hint() {
    return this.parallelism_hint;
  }

  public ComponentCommon setParallelism_hint(int parallelism_hint) {
    this.parallelism_hint = parallelism_hint;
    setParallelism_hintIsSet(true);
    return this;
  }

  public void unsetParallelism_hint() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __PARALLELISM_HINT_ISSET_ID);
  }

  /** Returns true if field parallelism_hint is set (has been assigned a value) and false otherwise */
  public boolean isSetParallelism_hint() {
    return EncodingUtils.testBit(__isset_bitfield, __PARALLELISM_HINT_ISSET_ID);
  }

  public void setParallelism_hintIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __PARALLELISM_HINT_ISSET_ID, value);
  }

  public String getJson_conf() {
    return this.json_conf;
  }

  public ComponentCommon setJson_conf(String json_conf) {
    this.json_conf = json_conf;
    return this;
  }

  public void unsetJson_conf() {
    this.json_conf = null;
  }

  /** Returns true if field json_conf is set (has been assigned a value) and false otherwise */
  public boolean isSetJson_conf() {
    return this.json_conf != null;
  }

  public void setJson_confIsSet(boolean value) {
    if (!value) {
      this.json_conf = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case INPUTS:
      if (value == null) {
        unsetInputs();
      } else {
        setInputs((Map<GlobalStreamId,Grouping>)value);
      }
      break;

    case STREAMS:
      if (value == null) {
        unsetStreams();
      } else {
        setStreams((Map<String,StreamInfo>)value);
      }
      break;

    case PARALLELISM_HINT:
      if (value == null) {
        unsetParallelism_hint();
      } else {
        setParallelism_hint((Integer)value);
      }
      break;

    case JSON_CONF:
      if (value == null) {
        unsetJson_conf();
      } else {
        setJson_conf((String)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case INPUTS:
      return getInputs();

    case STREAMS:
      return getStreams();

    case PARALLELISM_HINT:
      return Integer.valueOf(getParallelism_hint());

    case JSON_CONF:
      return getJson_conf();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case INPUTS:
      return isSetInputs();
    case STREAMS:
      return isSetStreams();
    case PARALLELISM_HINT:
      return isSetParallelism_hint();
    case JSON_CONF:
      return isSetJson_conf();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof ComponentCommon)
      return this.equals((ComponentCommon)that);
    return false;
  }

  public boolean equals(ComponentCommon that) {
    if (that == null)
      return false;

    boolean this_present_inputs = true && this.isSetInputs();
    boolean that_present_inputs = true && that.isSetInputs();
    if (this_present_inputs || that_present_inputs) {
      if (!(this_present_inputs && that_present_inputs))
        return false;
      if (!this.inputs.equals(that.inputs))
        return false;
    }

    boolean this_present_streams = true && this.isSetStreams();
    boolean that_present_streams = true && that.isSetStreams();
    if (this_present_streams || that_present_streams) {
      if (!(this_present_streams && that_present_streams))
        return false;
      if (!this.streams.equals(that.streams))
        return false;
    }

    boolean this_present_parallelism_hint = true && this.isSetParallelism_hint();
    boolean that_present_parallelism_hint = true && that.isSetParallelism_hint();
    if (this_present_parallelism_hint || that_present_parallelism_hint) {
      if (!(this_present_parallelism_hint && that_present_parallelism_hint))
        return false;
      if (this.parallelism_hint != that.parallelism_hint)
        return false;
    }

    boolean this_present_json_conf = true && this.isSetJson_conf();
    boolean that_present_json_conf = true && that.isSetJson_conf();
    if (this_present_json_conf || that_present_json_conf) {
      if (!(this_present_json_conf && that_present_json_conf))
        return false;
      if (!this.json_conf.equals(that.json_conf))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  public int compareTo(ComponentCommon other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;
    ComponentCommon typedOther = (ComponentCommon)other;

    lastComparison = Boolean.valueOf(isSetInputs()).compareTo(typedOther.isSetInputs());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetInputs()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.inputs, typedOther.inputs);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetStreams()).compareTo(typedOther.isSetStreams());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetStreams()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.streams, typedOther.streams);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetParallelism_hint()).compareTo(typedOther.isSetParallelism_hint());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetParallelism_hint()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.parallelism_hint, typedOther.parallelism_hint);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetJson_conf()).compareTo(typedOther.isSetJson_conf());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetJson_conf()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.json_conf, typedOther.json_conf);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("ComponentCommon(");
    boolean first = true;

    sb.append("inputs:");
    if (this.inputs == null) {
      sb.append("null");
    } else {
      sb.append(this.inputs);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("streams:");
    if (this.streams == null) {
      sb.append("null");
    } else {
      sb.append(this.streams);
    }
    first = false;
    if (isSetParallelism_hint()) {
      if (!first) sb.append(", ");
      sb.append("parallelism_hint:");
      sb.append(this.parallelism_hint);
      first = false;
    }
    if (isSetJson_conf()) {
      if (!first) sb.append(", ");
      sb.append("json_conf:");
      if (this.json_conf == null) {
        sb.append("null");
      } else {
        sb.append(this.json_conf);
      }
      first = false;
    }
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (inputs == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'inputs' was not present! Struct: " + toString());
    }
    if (streams == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'streams' was not present! Struct: " + toString());
    }
    // check for sub-struct validity
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
    try {
      // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
      __isset_bitfield = 0;
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class ComponentCommonStandardSchemeFactory implements SchemeFactory {
    public ComponentCommonStandardScheme getScheme() {
      return new ComponentCommonStandardScheme();
    }
  }

  private static class ComponentCommonStandardScheme extends StandardScheme<ComponentCommon> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, ComponentCommon struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // INPUTS
            if (schemeField.type == org.apache.thrift.protocol.TType.MAP) {
              {
                org.apache.thrift.protocol.TMap _map24 = iprot.readMapBegin();
                struct.inputs = new HashMap<GlobalStreamId,Grouping>(2*_map24.size);
                for (int _i25 = 0; _i25 < _map24.size; ++_i25)
                {
                  GlobalStreamId _key26; // required
                  Grouping _val27; // required
                  _key26 = new GlobalStreamId();
                  _key26.read(iprot);
                  _val27 = new Grouping();
                  _val27.read(iprot);
                  struct.inputs.put(_key26, _val27);
                }
                iprot.readMapEnd();
              }
              struct.setInputsIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // STREAMS
            if (schemeField.type == org.apache.thrift.protocol.TType.MAP) {
              {
                org.apache.thrift.protocol.TMap _map28 = iprot.readMapBegin();
                struct.streams = new HashMap<String,StreamInfo>(2*_map28.size);
                for (int _i29 = 0; _i29 < _map28.size; ++_i29)
                {
                  String _key30; // required
                  StreamInfo _val31; // required
                  _key30 = iprot.readString();
                  _val31 = new StreamInfo();
                  _val31.read(iprot);
                  struct.streams.put(_key30, _val31);
                }
                iprot.readMapEnd();
              }
              struct.setStreamsIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // PARALLELISM_HINT
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.parallelism_hint = iprot.readI32();
              struct.setParallelism_hintIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // JSON_CONF
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.json_conf = iprot.readString();
              struct.setJson_confIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in the validate method
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, ComponentCommon struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.inputs != null) {
        oprot.writeFieldBegin(INPUTS_FIELD_DESC);
        {
          oprot.writeMapBegin(new org.apache.thrift.protocol.TMap(org.apache.thrift.protocol.TType.STRUCT, org.apache.thrift.protocol.TType.STRUCT, struct.inputs.size()));
          for (Map.Entry<GlobalStreamId, Grouping> _iter32 : struct.inputs.entrySet())
          {
            _iter32.getKey().write(oprot);
            _iter32.getValue().write(oprot);
          }
          oprot.writeMapEnd();
        }
        oprot.writeFieldEnd();
      }
      if (struct.streams != null) {
        oprot.writeFieldBegin(STREAMS_FIELD_DESC);
        {
          oprot.writeMapBegin(new org.apache.thrift.protocol.TMap(org.apache.thrift.protocol.TType.STRING, org.apache.thrift.protocol.TType.STRUCT, struct.streams.size()));
          for (Map.Entry<String, StreamInfo> _iter33 : struct.streams.entrySet())
          {
            oprot.writeString(_iter33.getKey());
            _iter33.getValue().write(oprot);
          }
          oprot.writeMapEnd();
        }
        oprot.writeFieldEnd();
      }
      if (struct.isSetParallelism_hint()) {
        oprot.writeFieldBegin(PARALLELISM_HINT_FIELD_DESC);
        oprot.writeI32(struct.parallelism_hint);
        oprot.writeFieldEnd();
      }
      if (struct.json_conf != null) {
        if (struct.isSetJson_conf()) {
          oprot.writeFieldBegin(JSON_CONF_FIELD_DESC);
          oprot.writeString(struct.json_conf);
          oprot.writeFieldEnd();
        }
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class ComponentCommonTupleSchemeFactory implements SchemeFactory {
    public ComponentCommonTupleScheme getScheme() {
      return new ComponentCommonTupleScheme();
    }
  }

  private static class ComponentCommonTupleScheme extends TupleScheme<ComponentCommon> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, ComponentCommon struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      {
        oprot.writeI32(struct.inputs.size());
        for (Map.Entry<GlobalStreamId, Grouping> _iter34 : struct.inputs.entrySet())
        {
          _iter34.getKey().write(oprot);
          _iter34.getValue().write(oprot);
        }
      }
      {
        oprot.writeI32(struct.streams.size());
        for (Map.Entry<String, StreamInfo> _iter35 : struct.streams.entrySet())
        {
          oprot.writeString(_iter35.getKey());
          _iter35.getValue().write(oprot);
        }
      }
      BitSet optionals = new BitSet();
      if (struct.isSetParallelism_hint()) {
        optionals.set(0);
      }
      if (struct.isSetJson_conf()) {
        optionals.set(1);
      }
      oprot.writeBitSet(optionals, 2);
      if (struct.isSetParallelism_hint()) {
        oprot.writeI32(struct.parallelism_hint);
      }
      if (struct.isSetJson_conf()) {
        oprot.writeString(struct.json_conf);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, ComponentCommon struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      {
        org.apache.thrift.protocol.TMap _map36 = new org.apache.thrift.protocol.TMap(org.apache.thrift.protocol.TType.STRUCT, org.apache.thrift.protocol.TType.STRUCT, iprot.readI32());
        struct.inputs = new HashMap<GlobalStreamId,Grouping>(2*_map36.size);
        for (int _i37 = 0; _i37 < _map36.size; ++_i37)
        {
          GlobalStreamId _key38; // required
          Grouping _val39; // required
          _key38 = new GlobalStreamId();
          _key38.read(iprot);
          _val39 = new Grouping();
          _val39.read(iprot);
          struct.inputs.put(_key38, _val39);
        }
      }
      struct.setInputsIsSet(true);
      {
        org.apache.thrift.protocol.TMap _map40 = new org.apache.thrift.protocol.TMap(org.apache.thrift.protocol.TType.STRING, org.apache.thrift.protocol.TType.STRUCT, iprot.readI32());
        struct.streams = new HashMap<String,StreamInfo>(2*_map40.size);
        for (int _i41 = 0; _i41 < _map40.size; ++_i41)
        {
          String _key42; // required
          StreamInfo _val43; // required
          _key42 = iprot.readString();
          _val43 = new StreamInfo();
          _val43.read(iprot);
          struct.streams.put(_key42, _val43);
        }
      }
      struct.setStreamsIsSet(true);
      BitSet incoming = iprot.readBitSet(2);
      if (incoming.get(0)) {
        struct.parallelism_hint = iprot.readI32();
        struct.setParallelism_hintIsSet(true);
      }
      if (incoming.get(1)) {
        struct.json_conf = iprot.readString();
        struct.setJson_confIsSet(true);
      }
    }
  }

}

