import requests
import re
from urllib3 import urlparse

link_re = re.compile(r'href="(.*?)"')
urllist = []
lines_seen = set() # holds lines already seen

def crawl(url, maxlevel):
    # Limit the recursion, we're not downloading the whole Internet
    if(maxlevel == 0):
        return

    # Get the webpage
    req = requests.get(url)
    result = []

    if not url.startswith("http"):
        urllist.remove(url)
        return

    # Check if successful
    if(req.status_code != 200):
        urllist.remove(url)
        return

    # Find and follow all the links
    links = link_re.findall(req.text)
    for link in links:
        # Get an absolute URL for a link
        link = urlparse.urljoin(url, link)

        if (link == "#") or ("javascript:void(0)" in link) or (link == "tel:1877906-7957"):
            continue

        if link.endswith("/"):
            link = link[:-1]

        #print link
        urllist.append(link)
        crawl(link, maxlevel - 1)

crawl('https://google.com', 2)

outfile = open('google.txt', 'w')
result = list(set(urllist))
for data in result:
    
    outfile.write(data.encode("UTF-8"))
    outfile.write('\n')

outfile.close()


