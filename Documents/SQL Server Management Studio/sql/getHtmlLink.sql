select top 500 'https://www.sec.gov/Archives/edgar/data/'+convert(varchar(10),s.cik)+'/'+replace(s.adsh,'-','')+'/'+d.sFilename url, s.adsh 
from norm1.SubCache s inner join raw0.SubDocument d on s.adsh=d.fkSubmission and s.form=d.sType 
left join norm2_ops.MDA m on s.adsh=m.adsh where m.adsh is null 
and filed>20171231 and form in ('10-K','10-K/A','10-Q','10-Q/A','20-F','20-F/A','40-F','40-F/A','S-1','S-1/A') and right(sFilename,4)='.htm'