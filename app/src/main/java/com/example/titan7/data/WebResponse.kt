package com.example.titan7.data


import com.google.gson.annotations.SerializedName

data class WebResponse(
    @SerializedName("acd")
    val acd: Int,
    @SerializedName("baf")
    val baf: Int,
    @SerializedName("bap")
    val bap: Int,
    @SerializedName("bas")
    val bas: Int,
    @SerializedName("base_contract_code")
    val baseContractCode: String,
    @SerializedName("base_currency")
    val baseCurrency: String,
    @SerializedName("base_ltr")
    val baseLtr: String,
    @SerializedName("bat")
    val bat: String,
    @SerializedName("bbf")
    val bbf: Int,
    @SerializedName("bbp")
    val bbp: Int,
    @SerializedName("bbs")
    val bbs: Int,
    @SerializedName("bbt")
    val bbt: String,
    @SerializedName("c")
    val c: String,
    @SerializedName("chg")
    val chg: Double,
    @SerializedName("chg110")
    val chg110: Double,
    @SerializedName("chg22")
    val chg22: Double,
    @SerializedName("chg220")
    val chg220: Double,
    @SerializedName("chg5")
    val chg5: Double,
    @SerializedName("ClosePrice")
    val closePrice: Double,
    @SerializedName("codesub_nm")
    val codesubNm: String,
    @SerializedName("cpn")
    val cpn: Int,
    @SerializedName("cpp")
    val cpp: Int,
    @SerializedName("delta")
    val delta: Int,
    @SerializedName("dpb")
    val dpb: Int,
    @SerializedName("dps")
    val dps: Int,
    @SerializedName("emitent_type")
    val emitentType: String,
    @SerializedName("fv")
    val fv: Int,
    @SerializedName("gamma")
    val gamma: Int,
    @SerializedName("init")
    val `init`: Int,
    @SerializedName("ipo")
    val ipo: String,
    @SerializedName("issue_nb")
    val issueNb: String,
    @SerializedName("kind")
    val kind: Int,
    @SerializedName("ltp")
    val ltp: Double,
    @SerializedName("ltr")
    val ltr: String,
    @SerializedName("lts")
    val lts: Int,
    @SerializedName("ltt")
    val ltt: String,
    @SerializedName("marketStatus")
    val marketStatus: String,
    @SerializedName("maxtp")
    val maxtp: Double,
    @SerializedName("min_step")
    val minStep: Double,
    @SerializedName("mintp")
    val mintp: Double,
    @SerializedName("mrg")
    val mrg: String,
    @SerializedName("mtd")
    val mtd: String,
    @SerializedName("n")
    val n: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("name2")
    val name2: String,
    @SerializedName("ncd")
    val ncd: String,
    @SerializedName("ncp")
    val ncp: Int,
    @SerializedName("op")
    val op: Double,
    @SerializedName("option_type")
    val optionType: String,
    @SerializedName("otc_instr")
    val otcInstr: String,
    @SerializedName("p110")
    val p110: Double,
    @SerializedName("p22")
    val p22: Double,
    @SerializedName("p220")
    val p220: Double,
    @SerializedName("p5")
    val p5: Double,
    @SerializedName("pcp")
    val pcp: Double,
    @SerializedName("pp")
    val pp: Double,
    @SerializedName("quote_basis")
    val quoteBasis: String = "UNKNOWN_SYMBOL" ,
    @SerializedName("rev")
    val rev: Int,
    @SerializedName("scheme_calc")
    val schemeCalc: String,
    @SerializedName("step_price")
    val stepPrice: Double,
    @SerializedName("strike_price")
    val strikePrice: Int,
    @SerializedName("theta")
    val theta: Int,
    @SerializedName("trades")
    val trades: Int,
    @SerializedName("TradingReferencePrice")
    val tradingReferencePrice: Int,
    @SerializedName("TradingSessionSubID")
    val tradingSessionSubID: String,
    @SerializedName("type")
    val type: Int,
    @SerializedName("UTCOffset")
    val uTCOffset: Int,
    @SerializedName("virt_base_instr")
    val virtBaseInstr: String,
    @SerializedName("vlt")
    val vlt: Double,
    @SerializedName("vol")
    val vol: Int,
    @SerializedName("Volatility")
    val volatility: Int,
    @SerializedName("x_agg_futures")
    val xAggFutures: String,
    @SerializedName("x_curr")
    val xCurr: String,
    @SerializedName("x_currVal")
    val xCurrVal: Double,
    @SerializedName("x_descr")
    val xDescr: String,
    @SerializedName("x_dsc1")
    val xDsc1: Int,
    @SerializedName("x_dsc1_reception")
    val xDsc1Reception: String,
    @SerializedName("x_dsc2")
    val xDsc2: Int,
    @SerializedName("x_dsc2_reception")
    val xDsc2Reception: String,
    @SerializedName("x_dsc3")
    val xDsc3: Int,
    @SerializedName("x_istrade")
    val xIstrade: Int,
    @SerializedName("x_lot")
    val xLot: Int,
    @SerializedName("x_max")
    val xMax: Double,
    @SerializedName("x_min")
    val xMin: Int,
    @SerializedName("x_min_lot_q")
    val xMinLotQ: Int,
    @SerializedName("x_short")
    val xShort: Int,
    @SerializedName("x_short_reception")
    val xShortReception: String,
    @SerializedName("yld")
    val yld: Int,
    @SerializedName("yld_ytm_ask")
    val yldYtmAsk: Int,
    @SerializedName("yld_ytm_bid")
    val yldYtmBid: Int
)